package alainvanhout.rest.services;

import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.*;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.BasicScope;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.scope.SimpleScope;
import alainvanhout.rest.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScopeManager {

    private Map<ScopeContainer, Scope> instanceMap = new HashMap<>();
    private Map<Class, Scope> classMap = new HashMap<>();

    @Autowired
    private Collection<ScopeContainer> containers;

    @PostConstruct
    public void setup() {
        for (ScopeContainer scopeContainer : containers) {
            Scope scope = processScopeContainer(scopeContainer);
            instanceMap.put(scopeContainer, scope);
            classMap.put(scopeContainer.getClass(), scope);
        }
    }

    public RestResponse follow(ScopeContainer container, RestRequest restRequest) {
        if (!instanceMap.containsKey(container)) {
            throw new RestException("No ScopeContainer registered for " + container.getClass().getName());
        }
        return instanceMap.get(container).follow(restRequest);
    }

    public Scope getScopeForContainer(ScopeContainer container) {
        if (!instanceMap.containsKey(container)) {
            throw new RestException("No scope found for container " + container.getClass().getName());
        }
        return instanceMap.get(container);
    }

    public Scope processScopeContainer(ScopeContainer owner) {
        SimpleScope entityScope = new SimpleScope(this);
        SimpleScope instanceScope = new SimpleScope(this);
        entityScope.setFallbackScope(instanceScope);

        try {
            for (Method method : owner.getClass().getDeclaredMethods()) {
                checkForMapping(owner, method, method.getReturnType().equals(Void.TYPE), entityScope, instanceScope);
            }

            for (Field field : owner.getClass().getDeclaredFields()) {
                checkForMapping(owner, field, false, entityScope, instanceScope);
            }
        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + owner.getClass(), e);
        }
        return entityScope;
    }

    private void checkForMapping(ScopeContainer owner, AccessibleObject accessibleObject, boolean passing, SimpleScope entityScope, SimpleScope instanceScope) {
        RestEntity restEntity = ReflectionUtils.retrieveAnnotation(accessibleObject, RestEntity.class);
        RestRelative restRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestRelative.class);
        RestInstance restInstance = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstance.class);
        RestInstanceRelative restInstanceRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstanceRelative.class);
        RestError restError = ReflectionUtils.retrieveAnnotation(accessibleObject, RestError.class);
        RestEntityDefinition restEntityDefinition = ReflectionUtils.retrieveAnnotation(accessibleObject, RestEntityDefinition.class);

        if (restEntity != null) {
            addMapping(owner, accessibleObject, entityScope, restEntity.methods(), passing);
        }
        if (restRelative != null) {
            String value = restRelative.value();
            addRelativeMapping(owner, accessibleObject, entityScope, restRelative.methods(), passing, value);
        }

        if (restInstance != null) {
            addMapping(owner, accessibleObject, instanceScope, restInstance.methods(), passing);
        }
        if (restInstanceRelative != null) {
            String value = restInstanceRelative.value();
            addRelativeMapping(owner, accessibleObject, instanceScope, restInstanceRelative.methods(), passing, value);
        }

        if (restError != null) {
            addErrorMapping(owner, accessibleObject, entityScope, restError.methods());
            addErrorMapping(owner, accessibleObject, instanceScope, restError.methods());
        }
        if (restEntityDefinition != null) {
            Class entityClass = addEntityDefinition(owner, accessibleObject);
            entityScope.setDefinitionClass(entityClass);
            instanceScope.setDefinitionClass(entityClass);
        }
    }

    private Class addEntityDefinition(ScopeContainer owner, AccessibleObject accessibleObject) {
        try {
            Class entityClass = null;
            if (accessibleObject instanceof Field) {
                Field field = (Field) accessibleObject;
                field.setAccessible(true);
                Class type = field.getType();
                entityClass = (type.equals(Class.class)) ? (Class) field.get(owner) : type;
            }
            if (accessibleObject instanceof Method) {
                Object result = ((Method) accessibleObject).invoke(owner);
                entityClass = (result instanceof Class) ? (Class) result : result.getClass();
            }
            return entityClass;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RestException("Encountered an error when adding an entity definition", e);
        }
    }

    private void addErrorMapping(ScopeContainer owner, AccessibleObject accessibleObject, BasicScope scope, HttpMethod[] methods) {
        String name = ReflectionUtils.retrieveName(accessibleObject);
        RestMapping restMapping = new RestMapping(owner).set(accessibleObject);
        scope.addErrorMapping(restMapping, methods);
    }

    private void addMapping(ScopeContainer owner, AccessibleObject accessibleObject, BasicScope scope, HttpMethod[] methods, boolean passing) {
        String name = ReflectionUtils.retrieveName(accessibleObject);
        RestMapping restMapping = new RestMapping(owner).set(accessibleObject);
        if (passing) {
            scope.addPassMapping(restMapping, methods);
        } else {
            scope.addArriveMapping(restMapping, methods);
        }
    }

    private void addRelativeMapping(ScopeContainer owner, AccessibleObject accessibleObject, BasicScope scope, HttpMethod[] methods, boolean passing, String relative) {
        if (accessibleObject instanceof Field) {
            Field field = (Field) accessibleObject;
            if (!ScopeContainer.class.isAssignableFrom(field.getType())) {
                throw new RestException("Field for relative mapping is not supported for " + field.getType().getName());
            }
            try {
                field.setAccessible(true);
                ScopeContainer container = (ScopeContainer) field.get(owner);
                scope.addRelativeMapping(relative, new RestMapping(owner).scopeContainer(container));
            } catch (IllegalAccessException e) {
                throw new RestException("Encountered error while adding relative mapping for field " + field.getName());
            }
        } else if (accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;
            scope.addRelativeMapping(relative, new RestMapping(owner).method(method));
        } else {
            throw new RestException("Relative mapping does not support type " + accessibleObject.getClass().getName());
        }
    }
}
