package alainvanhout.rest.services;

import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.*;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.scope.GenericScope;
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
import java.util.function.Supplier;

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

    public Scope processScopeContainer(ScopeContainer container) {
        GenericScope entityScope = new GenericScope(this);
        entityScope.getDefinition().type("entity");
        GenericScope instanceScope = new GenericScope(this);
        instanceScope.getDefinition().type("instance");
        entityScope.setFallbackScope(instanceScope);

        try {
            for (Method method : container.getClass().getDeclaredMethods()) {
                checkForMapping(container, method, method.getReturnType().equals(Void.TYPE), entityScope, instanceScope);
            }

            for (Field field : container.getClass().getDeclaredFields()) {
                checkForMapping(container, field, false, entityScope, instanceScope);
            }
        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + container.getClass(), e);
        }

        RestEntityDefinition restEntityDefinition = ReflectionUtils.retrieveAnnotation(container.getClass(), RestEntityDefinition.class);
        if (restEntityDefinition != null) {
            instanceScope.getDefinition().name(restEntityDefinition.name()).internalClass(restEntityDefinition.instanceClass());
        }
        entityScope.getDefinition().addFallback("instance", instanceScope.getDefinition());

        return entityScope;
    }

    private void checkForMapping(ScopeContainer owner, AccessibleObject accessibleObject, boolean passing, GenericScope entityScope, GenericScope instanceScope) {
        RestEntity restEntity = ReflectionUtils.retrieveAnnotation(accessibleObject, RestEntity.class);
        RestRelative restRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestRelative.class);
        RestInstance restInstance = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstance.class);
        RestInstanceRelative restInstanceRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstanceRelative.class);
        RestError restError = ReflectionUtils.retrieveAnnotation(accessibleObject, RestError.class);

        Supplier supplier = () -> getScopeForContainer(owner);

        if (restEntity != null) {
            addToScope(entityScope, restEntity.methods(), passing, new RestMapping(owner).set(accessibleObject).setScopeSupplier(supplier));
        }
        if (restRelative != null) {
            addRelative(restRelative.value(), accessibleObject, owner, entityScope);
        }

        if (restInstance != null) {
            addToScope(instanceScope, restInstance.methods(), passing, new RestMapping(owner).set(accessibleObject).setScopeSupplier(supplier));
        }
        if (restInstanceRelative != null) {
//            instanceScope.addRelativeMapping(restInstanceRelative.value(), new RestMapping(owner).set(accessibleObject).setScopeSupplier(supplier));
            addRelative(restInstanceRelative.value(), accessibleObject, owner, instanceScope);
        }

        if (restError != null) {
            entityScope.addErrorMapping(new RestMapping(owner).set(accessibleObject), restError.methods());
            instanceScope.addErrorMapping(new RestMapping(owner).set(accessibleObject), restError.methods());
        }
    }

    private Supplier<Scope> toScopeSupplier(ScopeContainer owner, Field field) {
        if (!ScopeContainer.class.isAssignableFrom(field.getType())) {
            throw new RestException("Field for relative mapping is not supported for " + field.getType().getName());
        }
        try {
            field.setAccessible(true);
            ScopeContainer container = (ScopeContainer) field.get(owner);
            return () -> this.getScopeForContainer(container);
        } catch (IllegalAccessException e) {
            throw new RestException("Encountered error while adding relative mapping for field " + field.getName());
        }
    }

    private void addRelative(String relative, AccessibleObject accessibleObject, ScopeContainer owner, GenericScope scope) {
        if (accessibleObject instanceof Field) {
            scope.addRelativeScope(relative, toScopeSupplier(owner, (Field) accessibleObject));
        } else if (accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;
            scope.addRelativeMapping(relative, new RestMapping(owner).method(method));
        } else {
            throw new RestException("Relative mapping does not support type " + accessibleObject.getClass().getName());
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

    private void addToScope(Scope scope, HttpMethod[] methods, boolean passing, RestMapping mapping) {
        if (passing) {
            scope.addPassMapping(mapping, methods);
        } else {
            scope.addArriveMapping(mapping, methods);
        }
    }

    private RestMapping createRelativeMapping(ScopeContainer owner, AccessibleObject accessibleObject) {
        if (accessibleObject instanceof Field) {
            Field field = (Field) accessibleObject;
            if (!ScopeContainer.class.isAssignableFrom(field.getType())) {
                throw new RestException("Field for relative mapping is not supported for " + field.getType().getName());
            }
            try {
                field.setAccessible(true);
                ScopeContainer container = (ScopeContainer) field.get(owner);
                return new RestMapping(owner).setScopeSupplier(() -> this.getScopeForContainer(container));
            } catch (IllegalAccessException e) {
                throw new RestException("Encountered error while adding relative mapping for field " + field.getName());
            }
        } else if (accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;
            return new RestMapping(owner).method(method);
        } else {
            throw new RestException("Relative mapping does not support type " + accessibleObject.getClass().getName());
        }
    }
}
