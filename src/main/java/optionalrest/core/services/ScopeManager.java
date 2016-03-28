package optionalrest.core.services;

import optionalrest.core.RestException;
import optionalrest.core.annotations.Error;
import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.annotations.requests.RequestHandler;
import optionalrest.core.annotations.scopes.Entity;
import optionalrest.core.annotations.scopes.Instance;
import optionalrest.core.annotations.scopes.Relative;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.Scope;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.mapping.AnnotationBundle;
import optionalrest.core.services.mapping.MethodMapping;
import optionalrest.core.services.mapping.providers.ParameterMapperProvider;
import optionalrest.core.services.mapping.providers.ResponseConverterProvider;
import optionalrest.core.utils.ReflectionUtils;
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
import java.util.List;
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
        AnnotationBundle bundle = retrieveAnnotationBundle(accessibleObject);

        if (isRequestHandler(bundle)) {
            bundle.add(container.getClass().getAnnotations());
            if (checkInstanceRelative(container, accessibleObject, bundle)) {
                return;
            }
            if (checkInstance(container, accessibleObject, bundle)) {
                return;
            }

            if (checkRelative(container, accessibleObject, bundle)) {
                return;
            }
            checkForScope(accessibleObject, container, bundle);
        }
        if (bundle.contains(Error.class)){
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

    public boolean checkInstanceRelative(ScopeContainer container, AccessibleObject accessibleObject, AnnotationBundle bundle) {
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

        String relativeId = scopeHelper.retrieveRelativeScopeId(accessibleObject);
        Scope relativeScope = scopeRegistry.produceScope(relativeId, null);
        instanceScope.addRelativeScope(relativePath, relativeScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            addMappingForMethod(accessibleObject, container, relativeScope, bundle);
        }

        return true;
    }

    public boolean checkInstance(ScopeContainer container, AccessibleObject accessibleObject, AnnotationBundle bundle) {
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
            addMappingForMethod(accessibleObject, container, instanceScope, bundle);
        }

        return true;
    }

    public boolean checkRelative(ScopeContainer container, AccessibleObject accessibleObject, AnnotationBundle bundle) {
        Relative relative = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Relative.class);
        if (relative == null) {
            return false;
        }

        String relativePath = relative.path();

        String parentId = scopeHelper.retrieveScopeId(container.getClass());
        Scope parentScope = scopeRegistry.produceScope(parentId, container);

        String relativeId = scopeHelper.retrieveRelativeScopeId(accessibleObject);
        Scope relativeScope = scopeRegistry.produceScope(relativeId, null);
        parentScope.addRelativeScope(relativePath, relativeScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            addMappingForMethod(accessibleObject, container, relativeScope, bundle);
        }

        return true;
    }

    private boolean isRequestHandler(AnnotationBundle bundle) {
        return bundle.contains(RequestHandler.class);
    }

    private AnnotationBundle retrieveAnnotationBundle(AccessibleObject accessibleObject) {
        AnnotationBundle bundle = new AnnotationBundle();
        bundle.add(accessibleObject.getAnnotations());
        return bundle;
    }

    public void addMappingForMethod(AccessibleObject accessibleObject, ScopeContainer container, Scope scope, AnnotationBundle bundle) {
        List<Annotation> handles = bundle.subList(Handle.class);

        if (accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;

            MethodMapping mapping = new MethodMapping(container, method);
            scope.addPassMapping(mapping);

            mapping.passing(Void.TYPE.equals(method.getReturnType()));
            mapping.order(Void.TYPE.equals(method.getReturnType()) ? 0 : 1);

            // scope helper provides defaults if necessary

            for (Annotation handle : handles) {
                scopeHelper.updateSupported(mapping, (Handle)handle);
            }
            mapping.responseTypeMappers(responseTypeMappers).parameterMappers(parameterMappers);
        }
    }

    public void checkForScope(AccessibleObject accessibleObject, ScopeContainer container, AnnotationBundle bundle) {
        String scopeId = getScopeId(accessibleObject, container);
        Scope scope = scopeRegistry.produceScope(scopeId, container);
        addMappingForMethod(accessibleObject, container, scope, bundle);
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
