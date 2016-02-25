package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.resource.RestError;
import alainvanhout.optionalrest.annotations.resource.RestRelative;
import alainvanhout.optionalrest.annotations.resource.RestScope;
import alainvanhout.optionalrest.request.RestRequest;
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
import java.util.Map;
import java.util.function.Function;

@Service
public class ResourceScopeFactory implements ScopeFactory {

    public static final String RESOURCE = "resource";

    @Autowired
    private ScopeRegistry scopeRegistry;

    @Override
    public void processContainer(ScopeContainer container, Map<Class, Function<RestRequest, Object>> parameterMappers) {

        try {
            for (Method method : container.getClass().getDeclaredMethods()) {
                Mapping mapping = new MethodMapping(container, method).parameterMappers(parameterMappers);
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
        RestScope annRestScope = ReflectionUtils.retrieveAnnotation(accessibleObject, RestScope.class);
        RestRelative annRestRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestRelative.class);
        RestError annRestError = ReflectionUtils.retrieveAnnotation(accessibleObject, RestError.class);

        // pass and arrive mapping
        if (annRestScope != null) {
            String scopeId = ScopeFactoryUtils.determineParentName(annRestScope.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeId, container);
            if (passing) {
                scope.addPassMapping(mapping, annRestScope.methods());
            } else {
                scope.addArriveMapping(mapping, annRestScope.methods());
            }
        }

        // error mapping
        if (annRestError != null) {
            String scopeId = ScopeFactoryUtils.determineParentName(annRestError.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeId, container);
            scope.addErrorMapping(mapping, annRestError.methods());
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
                if (passing) {
                    relativeScope.addPassMapping(mapping, annRestRelative.methods());
                } else {
                    relativeScope.addArriveMapping(mapping, annRestRelative.methods());
                }
            }

            parentScope.addRelativeScope(relative, relativeScope);
        }
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
