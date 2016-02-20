package alainvanhout.rest.services.factories;

import alainvanhout.rest.RestException;
import alainvanhout.rest.annotations.resource.RestError;
import alainvanhout.rest.annotations.resource.RestRelative;
import alainvanhout.rest.annotations.resource.RestScope;
import alainvanhout.rest.scope.GenericScope;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.services.ScopeRegistry;
import alainvanhout.rest.services.mapping.Mapping;
import alainvanhout.rest.services.mapping.MethodMapping;
import alainvanhout.rest.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Service
public class ResourceScopeFactory implements ScopeFactory {

    public static final String RESOURCE = "resource";

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
//                Mapping mapping = new ScopeMapping(() -> scopeRegistry.findByName(field.getType().getCanonicalName()));
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
            String scopeName = determineParentName(annRestScope.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeName, container, RESOURCE);
            if (passing) {
                scope.addPassMapping(mapping, annRestScope.methods());
            } else {
                scope.addArriveMapping(mapping, annRestScope.methods());
            }
        }

        // error mapping
        if (annRestError != null) {
            String scopeName = determineParentName(annRestError.scope(), container);
            Scope scope = scopeRegistry.produceScope(scopeName, container, RESOURCE);
            scope.addErrorMapping(mapping, annRestError.methods());
        }

        // relative scopes
        if (annRestRelative != null) {
            String relative = annRestRelative.path();

            // parentName: either custom or container class name
            String parentName = determineParentName(annRestRelative.parentScope(), container);
            Scope parentScope = scopeRegistry.produceScope(parentName, container, RESOURCE);
            // relativeName: either custom or parentName with suffix that includes relative path
            String relativeName = getRelativeName(accessibleObject, relative, parentName, annRestRelative.relativeScope());
            Scope relativeScope = scopeRegistry.produceScope(relativeName, null, RESOURCE);

            if (accessibleObject instanceof Method){
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
            return determineRelativeName(customRelativeName, relative, parentName);
        }
        if (accessibleObject instanceof Field) {
            if (ScopeContainer.class.isAssignableFrom(((Field) accessibleObject).getType())) {
                return ((Field) accessibleObject).getType().getCanonicalName();
            }
        }
        throw new RestException("Type not supported: " + accessibleObject.getClass());
    }

    private Scope retrieveScope(String scopeName, ScopeContainer container) {
        Scope scope = scopeRegistry.findByName(scopeName);
        if (scope == null) {
            scope = new GenericScope();
            scope.getDefinition().name(scopeName).type(RESOURCE);
            scopeRegistry.add(scopeName, scope);
            if (container != null) {
                scopeRegistry.add(container, scope);
            }
        }
        return scope;
    }

    private String determineParentName(String parentName, ScopeContainer container) {
        if (StringUtils.isBlank(parentName)) {
            return container.getClass().getCanonicalName();
        }
        return parentName;
    }

    private String determineRelativeName(String childName, String relative, String parentName) {
        if (StringUtils.isBlank(childName)) {
            return parentName + ":" + relative;
        }
        return childName;
    }
}
