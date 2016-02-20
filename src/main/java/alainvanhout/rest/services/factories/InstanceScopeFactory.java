package alainvanhout.rest.services.factories;

import alainvanhout.rest.RestException;
import alainvanhout.rest.annotations.instance.RestInstance;
import alainvanhout.rest.annotations.instance.RestInstanceRelative;
import alainvanhout.rest.annotations.resource.RestError;
import alainvanhout.rest.scope.GenericScope;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.services.ScopeRegistry;
import alainvanhout.rest.services.mapping.Mapping;
import alainvanhout.rest.services.mapping.MethodMapping;
import alainvanhout.rest.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Service
public class InstanceScopeFactory implements ScopeFactory {

    public static final String INSTANCE = "instance";
    @Autowired
    private ScopeRegistry scopeRegistry;

    @Override
    public void processContainer(ScopeContainer container) {

        try {
            for (Method method : container.getClass().getDeclaredMethods()) {
                Mapping mapping = new MethodMapping(container, method);
                processAccessibleObject(container, method, mapping, method.getReturnType().equals(Void.TYPE));
            }

            for (Field field : container.getClass().getDeclaredFields()) {
                processAccessibleObject(container, field, null, false);
            }
        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + container.getClass(), e);
        }
    }

    private void processAccessibleObject(ScopeContainer container, AccessibleObject accessibleObject, Mapping mapping, boolean passing) {
        RestInstance annRestInstance = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstance.class);
        RestInstanceRelative annRestInstanceRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstanceRelative.class);
        RestError annRestError = ReflectionUtils.retrieveAnnotation(accessibleObject, RestError.class);

        if (annRestInstance != null) {
            String parentName = ScopeFactoryUtils.determineParentName(annRestInstance.parentScope(), container);
            Scope parentScope = produceEntityScope(parentName);

            String instanceName = getInstanceName(accessibleObject, parentName, annRestInstance.instanceScope());
            Scope instanceScope = produceInstanceScope(instanceName);
            parentScope.setInstanceScope(instanceScope);

            if (accessibleObject instanceof Method) {
                if (passing) {
                    instanceScope.addPassMapping(mapping, annRestInstance.methods());
                } else {
                    instanceScope.addArriveMapping(mapping, annRestInstance.methods());
                }
            } else {
                throw new RestException("Type of accessible object not supported: " + accessibleObject.getClass().getCanonicalName());
            }

        }
        if (annRestInstanceRelative != null) {
            String relative = annRestInstanceRelative.path();

            String parentName = ScopeFactoryUtils.determineParentName(annRestInstanceRelative.parentScope(), container);
            Scope parentScope = produceEntityScope(parentName);

            String instanceName = ScopeFactoryUtils.determineInstanceName(annRestInstanceRelative.instanceScope(), parentName);
            Scope instanceScope = produceInstanceScope(instanceName);

            String relativeName = getRelativeName(accessibleObject, relative, instanceName, annRestInstanceRelative.relativeScope());
            Scope relativeScope = produceInstanceScope(relativeName);

            if (accessibleObject instanceof Method) {
                if (passing) {
                    relativeScope.addPassMapping(mapping, annRestInstanceRelative.methods());
                } else {
                    relativeScope.addArriveMapping(mapping, annRestInstanceRelative.methods());
                }
            }

            parentScope.setInstanceScope(instanceScope);
            instanceScope.addRelativeScope(annRestInstanceRelative.path(), relativeScope);
        }

        // error mapping
        if (annRestError != null) {
            String scopeName = ScopeFactoryUtils.determineParentName(annRestError.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeName, container, ResourceScopeFactory.RESOURCE);
            scope.addErrorMapping(mapping, annRestError.methods());
        }
    }

    private String getInstanceName(AccessibleObject accessibleObject, String parentName, String customInstanceName) {
        if (accessibleObject instanceof Method) {
            return ScopeFactoryUtils.determineInstanceName(customInstanceName, parentName);
        }
        if (accessibleObject instanceof Field) {
            if (ScopeContainer.class.isAssignableFrom(((Field) accessibleObject).getType())) {
                return ((Field) accessibleObject).getType().getCanonicalName();
            }
        }
        throw new RestException("Type not supported: " + accessibleObject.getClass());
    }

    private String getRelativeName(AccessibleObject accessibleObject, String relative, String parentName, String customRelativeName) {
        if (accessibleObject instanceof Method) {
            return ScopeFactoryUtils.determineRelativeName(customRelativeName, relative, parentName);
        }
        if (accessibleObject instanceof Field) {
            if (ScopeContainer.class.isAssignableFrom(((Field) accessibleObject).getType())) {
                return ((Field) accessibleObject).getType().getCanonicalName();
            }
        }
        throw new RestException("Type not supported: " + accessibleObject.getClass());
    }

    private Scope produceEntityScope(String scopeName) {
        Scope scope = scopeRegistry.findByName(scopeName);
        if (scope == null) {
            scope = new GenericScope();
            scope.getDefinition().name(scopeName);
            scopeRegistry.add(scopeName, scope);
        }
        scope.getDefinition().type(EntityScopeFactory.ENTITY);
        return scope;
    }

    private Scope produceInstanceScope(String scopeName) {
        Scope scope = scopeRegistry.findByName(scopeName);
        if (scope == null) {
            scope = new GenericScope();
            scope.getDefinition().name(scopeName);
            scopeRegistry.add(scopeName, scope);
        }
        scope.getDefinition().type(INSTANCE);
        return scope;
    }
}
