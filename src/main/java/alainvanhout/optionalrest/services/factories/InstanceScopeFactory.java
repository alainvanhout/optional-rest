package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.RestEntityDefinition;
import alainvanhout.optionalrest.annotations.instance.RestInstance;
import alainvanhout.optionalrest.annotations.instance.RestInstanceRelative;
import alainvanhout.optionalrest.annotations.resource.RestError;
import alainvanhout.optionalrest.scope.GenericScope;
import alainvanhout.optionalrest.scope.Scope;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeRegistry;
import alainvanhout.optionalrest.services.mapping.Mapping;
import alainvanhout.optionalrest.services.mapping.MethodMapping;
import alainvanhout.optionalrest.utils.ReflectionUtils;
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

        RestEntityDefinition restEntityDefinition = ReflectionUtils.retrieveAnnotation(container.getClass(), RestEntityDefinition.class);
        if (restEntityDefinition != null) {
            String parentName = ScopeFactoryUtils.determineParentName(restEntityDefinition.entityScope(), container);
            Scope parentScope = produceEntityScope(parentName, container);

            String instanceName = ScopeFactoryUtils.determineInstanceName(restEntityDefinition.instanceScope(), parentName);
            Scope instanceScope = produceInstanceScope(instanceName);
            parentScope.setInstanceScope(instanceScope);

            instanceScope.getDefinition().setInternalClass(restEntityDefinition.instanceClass());
        }
    }

    private void processAccessibleObject(ScopeContainer container, AccessibleObject accessibleObject, Mapping mapping, boolean passing) {
        RestInstance annRestInstance = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstance.class);
        RestInstanceRelative annRestInstanceRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstanceRelative.class);
        RestError annRestError = ReflectionUtils.retrieveAnnotation(accessibleObject, RestError.class);

        if (annRestInstance != null) {
            String parentName = ScopeFactoryUtils.determineParentName(annRestInstance.parentScope(), container);
            Scope parentScope = produceEntityScope(parentName, container);

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
                // TODO?
                throw new RestException("Type of accessible object not supported: " + accessibleObject.getClass().getCanonicalName());
            }

        }
        if (annRestInstanceRelative != null) {
            String relative = annRestInstanceRelative.path();

            String parentName = ScopeFactoryUtils.determineParentName(annRestInstanceRelative.parentScope(), container);
            Scope parentScope = produceEntityScope(parentName, container);

            String instanceName = ScopeFactoryUtils.determineInstanceName(annRestInstanceRelative.instanceScope(), parentName);
            Scope instanceScope = produceInstanceScope(instanceName);
            parentScope.setInstanceScope(instanceScope);

            String relativeName = getRelativeName(accessibleObject, relative, instanceName, annRestInstanceRelative.relativeScope());
            Scope relativeScope = produceInstanceScope(relativeName);
            instanceScope.addRelativeScope(annRestInstanceRelative.path(), relativeScope);

            // only necessary for Method
            if (accessibleObject instanceof Method) {
                if (passing) {
                    relativeScope.addPassMapping(mapping, annRestInstanceRelative.methods());
                } else {
                    relativeScope.addArriveMapping(mapping, annRestInstanceRelative.methods());
                }
            }
        }

        // error mapping
        if (annRestError != null) {
            String scopeId = ScopeFactoryUtils.determineParentName(annRestError.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeId, container);
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

    private Scope produceEntityScope(String scopeId, ScopeContainer container) {
        Scope scope = scopeRegistry.produceScope(scopeId, container);
        scope.getDefinition().type(EntityScopeFactory.ENTITY);
        return scope;
    }

    private Scope produceInstanceScope(String scopeId) {
        Scope scope = scopeRegistry.findByName(scopeId);
        if (scope == null) {
            scope = new GenericScope().scopeId(scopeId);
            scopeRegistry.add(scopeId, scope);
        }
        scope.getDefinition().type(INSTANCE);
        return scope;
    }
}
