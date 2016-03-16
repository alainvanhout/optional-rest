package alainvanhout.optionalrest.services;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.*;
import alainvanhout.optionalrest.annotations.Error;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.Scope;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.mapping.MethodMapping;
import alainvanhout.optionalrest.services.mapping.providers.ParameterMapperProvider;
import alainvanhout.optionalrest.services.mapping.providers.ResponseConverterProvider;
import alainvanhout.optionalrest.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class ScopeManager {

    @Autowired
    private ScopeHelper scopeHelper;

    @Autowired
    private ScopeRegistry scopeRegistry;

    @Autowired
    private Collection<ScopeContainer> containers;

    @Autowired
    private Collection<ParameterMapperProvider> parameterMapperProviders;

    @Autowired
    private Collection<ResponseConverterProvider> responseConverterProviders;

    private Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> parameterMappers = new HashMap<>();

    private Map<Class, Function<Object, Object>> responseTypeMappers = new HashMap<>();

    @PostConstruct
    public void setup() {
        for (ParameterMapperProvider parameterMapperProvider : parameterMapperProviders) {
            parameterMappers.putAll(parameterMapperProvider.getCombinedParameterMappers());
        }

        for (ResponseConverterProvider responseConverterProvider : responseConverterProviders) {
            responseTypeMappers.putAll(responseConverterProvider.getResponseConverters());
        }

        for (ScopeContainer scopeContainer : containers) {
            processContainer(scopeContainer);
        }
    }

    public void processContainer(ScopeContainer container) {

        Entity entity = ReflectionUtils.retrieveAnnotation(container.getClass(), Entity.class);
        if (entity != null){
            String scopeId = scopeHelper.retrieveScopeId(container.getClass());
            if (StringUtils.isBlank(scopeId)) {
                throw new RestException("Cannot assign entity because no scopeId was found for " + container.getClass());
            }
            Scope scope = scopeRegistry.produceScope(scopeId, container);
            scope.getDefinition().setInternalClass(entity.value());
        }

        try {
            for (Method method : container.getClass().getDeclaredMethods()) {
                process(method, container);
            }

            for (Field field : container.getClass().getDeclaredFields()) {
                process(field, container);
            }
        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + container.getClass(), e);
        }
    }

    private void process(AccessibleObject accessibleObject, ScopeContainer container) {
        if (isRequestHandler(accessibleObject)) {
            if (checkInstanceRelative(container, accessibleObject)) {
                return;
            }
            if (checkInstance(container, accessibleObject)) {
                return;
            }

            if (checkRelative(container, accessibleObject)) {
                return;
            }
            checkForHandle(accessibleObject, container);
        }
        Error error = ReflectionUtils.retrieveAnnotation(accessibleObject, Error.class);
        if (error != null){
            String scopeId = scopeHelper.retrieveScopeId(accessibleObject, container.getClass());
            Scope scope = scopeRegistry.produceScope(scopeId, container);
            if (accessibleObject instanceof Method) {
                Method method = (Method) accessibleObject;
                MethodMapping mapping = new MethodMapping(container, method);
                scope.addErrorMapping(mapping);
                // scope helper provides defaults if necessary
                Handle handle = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Handle.class);
                scopeHelper.updateSupported(mapping, handle);
                mapping.responseTypeMappers(responseTypeMappers).parameterMappers(parameterMappers);
            }
        }
    }

    public boolean checkInstanceRelative(ScopeContainer container, AccessibleObject accessibleObject) {
        Relative relative = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Relative.class);
        Instance instance = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Instance.class);
        if (relative == null || instance == null) {
            return false;
        }

        String relativePath = relative.path();

        String parentId = scopeHelper.retrieveScopeId(container.getClass());
        Scope parentScope = scopeRegistry.produceScope(parentId, container);

        String instanceId = scopeHelper.retrieveInstanceScopeId(accessibleObject, container.getClass());
        Scope instanceScope = scopeRegistry.produceScope(instanceId, container);
        parentScope.setInstanceScope(instanceScope);

        String relativeId = scopeHelper.retrieveRelativeScopeId(accessibleObject, container.getClass());
        Scope relativeScope = scopeRegistry.produceScope(relativeId, null);
        instanceScope.addRelativeScope(relativePath, relativeScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            Handle handle = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Handle.class);
            addMappingForMethod(accessibleObject, container, relativeScope, handle);
        }

        return true;
    }

    public boolean checkInstance(ScopeContainer container, AccessibleObject accessibleObject) {
        Instance instance = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Instance.class);
        if (instance == null) {
            return false;
        }

        String parentId = scopeHelper.retrieveScopeId(container.getClass());
        Scope parentScope = scopeRegistry.produceScope(parentId, container);

        String instanceId = scopeHelper.retrieveInstanceScopeId(accessibleObject, container.getClass());
        Scope instanceScope = scopeRegistry.produceScope(instanceId, container);
        parentScope.setInstanceScope(instanceScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            Handle handle = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Handle.class);
            addMappingForMethod(accessibleObject, container, instanceScope, handle);
        }

        return true;
    }

    public boolean checkRelative(ScopeContainer container, AccessibleObject accessibleObject) {
        Relative relative = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Relative.class);
        if (relative == null) {
            return false;
        }

        String relativePath = relative.path();

        String parentId = scopeHelper.retrieveScopeId(container.getClass());
        Scope parentScope = scopeRegistry.produceScope(parentId, container);

        String relativeId = scopeHelper.retrieveRelativeScopeId(accessibleObject, container.getClass());
        Scope relativeScope = scopeRegistry.produceScope(relativeId, null);
        parentScope.addRelativeScope(relativePath, relativeScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            Handle handle = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Handle.class);
            addMappingForMethod(accessibleObject, container, relativeScope, handle);

        }

        return true;
    }

    private boolean isRequestHandler(AccessibleObject accessibleObject) {
        if (ReflectionUtils.retrieveAnnotation(accessibleObject, RequestHandler.class) != null) {
            return true;
        }
        for (Annotation annotation : accessibleObject.getAnnotations()) {
            if (annotation.annotationType().getDeclaredAnnotation(RequestHandler.class) != null) {
                return true;
            }
        }
        return false;
    }

    public void addMappingForMethod(AccessibleObject accessibleObject, ScopeContainer container, Scope scope, Handle handle) {
        if (accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;
            MethodMapping mapping = new MethodMapping(container, method);
            if (method.getReturnType().equals(Void.TYPE)) {
                scope.addPassMapping(mapping);
            } else {
                scope.addArriveMapping(mapping);
            }
            // scope helper provides defaults if necessary
            scopeHelper.updateSupported(mapping, handle);
            mapping.responseTypeMappers(responseTypeMappers).parameterMappers(parameterMappers);
        }
    }

    public void checkForHandle(AccessibleObject accessibleObject, ScopeContainer container) {
        String scopeId = getScopeId(accessibleObject, container);
        Scope scope = scopeRegistry.produceScope(scopeId, container);

        Handle handle = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Handle.class);
        addMappingForMethod(accessibleObject, container, scope, handle);
    }

    public String getScopeId(AccessibleObject accessibleObject, ScopeContainer container) {
        String scopeId = scopeHelper.retrieveScopeId(accessibleObject, container.getClass());
        if (StringUtils.isBlank(scopeId)) {
            throw new RestException(String.format("Could not determine scope id for %s in class %s",
                    accessibleObject, container.getClass().getCanonicalName()));
        }
        return scopeId;
    }

    public Response follow(ScopeContainer container, Request request) {
        return scopeRegistry.findByContainer(container).follow(request);
    }
}
