package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.Description;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.instance.RestInstance;
import alainvanhout.optionalrest.annotations.instance.RestInstanceRelative;
import alainvanhout.optionalrest.annotations.resource.RestError;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.GenericScope;
import alainvanhout.optionalrest.scope.Scope;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeRegistry;
import alainvanhout.optionalrest.services.mapping.Mapping;
import alainvanhout.optionalrest.services.mapping.MethodMapping;
import alainvanhout.optionalrest.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class InstanceScopeFactory implements ScopeFactory {

    public static final String INSTANCE = "instance";
    @Autowired
    private ScopeRegistry scopeRegistry;

    @Override
    public void processContainer(ScopeContainer container, Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> parameterMappers, Map<Class, Function<Object, Object>> responseTypeMappers) {
        try {
            for (Method method : container.getClass().getDeclaredMethods()) {
                MethodMapping mapping = new MethodMapping(container, method);
                if (processAccessibleObject(container, method, mapping, method.getReturnType().equals(Void.TYPE))) {
                    mapping.responseTypeMappers(responseTypeMappers).parameterMappers(parameterMappers);
                }
            }

            for (Field field : container.getClass().getDeclaredFields()) {
                processAccessibleObject(container, field, null, false);
            }
        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + container.getClass(), e);
        }

        ScopeDefinition scopeDefinition = ReflectionUtils.retrieveAnnotation(container.getClass(), ScopeDefinition.class);
        if (scopeDefinition != null) {
            String parentName = ScopeFactoryUtils.determineParentName(scopeDefinition.id(), container);
            Scope parentScope = produceEntityScope(parentName, container);
            parentScope.getDefinition().name(scopeDefinition.name());
        }

        EntityDefinition restEntityDefinition = ReflectionUtils.retrieveAnnotation(container.getClass(), EntityDefinition.class);
        if (restEntityDefinition != null) {
            Scope instanceScope = getInstanceScope(container, restEntityDefinition);
            instanceScope.getDefinition().setInternalClass(restEntityDefinition.instanceClass());
        }

        Description annDescription = ReflectionUtils.retrieveAnnotation(container.getClass(), Description.class);
        if (annDescription != null) {
            Scope instanceScope = getInstanceScope(container, restEntityDefinition);
            instanceScope.getDefinition().description(annDescription.value());
        }
    }

    public Scope getInstanceScope(ScopeContainer container, EntityDefinition restEntityDefinition) {
        String parentName = ScopeFactoryUtils.determineParentName(restEntityDefinition.entityScope(), container);
        Scope parentScope = produceEntityScope(parentName, container);

        String instanceName = ScopeFactoryUtils.determineInstanceName(restEntityDefinition.instanceScope(), parentName);
        Scope instanceScope = produceInstanceScope(instanceName);
        parentScope.setInstanceScope(instanceScope);
        return instanceScope;
    }

    private boolean processAccessibleObject(ScopeContainer container, AccessibleObject accessibleObject, Mapping mapping, boolean passing) {
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
            return true;
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
            return true;
        }

        // error mapping
        if (annRestError != null) {
            String scopeId = ScopeFactoryUtils.determineParentName(annRestError.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeId, container);
            scope.addErrorMapping(mapping, annRestError.methods());
            return true;
        }

        return false;
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
