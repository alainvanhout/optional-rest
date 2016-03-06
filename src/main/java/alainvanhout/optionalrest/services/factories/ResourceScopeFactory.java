package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.resource.RestError;
import alainvanhout.optionalrest.annotations.resource.RestRelative;
import alainvanhout.optionalrest.annotations.resource.RestScope;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.Scope;
import alainvanhout.optionalrest.scope.Supported;
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
public class ResourceScopeFactory implements ScopeFactory {

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
    }

    private boolean processAccessibleObject(ScopeContainer container, AccessibleObject accessibleObject, Mapping mapping, boolean passing) {
        RestScope annRestScope = ReflectionUtils.retrieveAnnotation(accessibleObject, RestScope.class);
        RestRelative annRestRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestRelative.class);
        RestError annRestError = ReflectionUtils.retrieveAnnotation(accessibleObject, RestError.class);

        // pass and arrive mapping
        if (annRestScope != null) {
            String scopeId = ScopeFactoryUtils.determineParentName(annRestScope.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeId, container);
            Supported supported = new Supported()
                    .methods(annRestScope.methods())
                    .accepts(annRestScope.accepts());
            if (passing) {
                scope.addPassMapping(mapping, supported);
            } else {
                scope.addArriveMapping(mapping, supported);
            }
            return true;
        }

        // error mapping
        if (annRestError != null) {
            String scopeId = ScopeFactoryUtils.determineParentName(annRestError.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeId, container);
            Supported supported = new Supported()
                    .methods(annRestError.methods());
            scope.addErrorMapping(mapping, supported);
            return true;
        }

        // relative scopes
        if (annRestRelative != null) {
            String relative = annRestRelative.path();

            String parentName = ScopeFactoryUtils.determineParentName(annRestRelative.parentScope(), container);
            Scope parentScope = scopeRegistry.produceScope(parentName, container);

            String relativeName = getRelativeName(accessibleObject, relative, parentName, annRestRelative.relativeScope());
            Scope relativeScope = scopeRegistry.produceScope(relativeName, null);

            // only necessary for Method
            if (accessibleObject instanceof Method) {
                Supported supported = new Supported()
                        .methods(annRestRelative.methods())
                        .accepts(annRestRelative.accepts());
                if (passing) {
                    relativeScope.addPassMapping(mapping, supported);
                } else {
                    relativeScope.addArriveMapping(mapping, supported);
                }
            }

            parentScope.addRelativeScope(relative, relativeScope);
            return true;
        }

        return false;
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

}
