package optionalrest.core.services;

import optionalrest.core.RestException;
import optionalrest.core.annotations.Error;
import optionalrest.core.annotations.Order;
import optionalrest.core.annotations.aop.After;
import optionalrest.core.annotations.aop.Before;
import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.annotations.requests.RequestHandler;
import optionalrest.core.annotations.scopes.Entity;
import optionalrest.core.annotations.scopes.Instance;
import optionalrest.core.annotations.scopes.Relative;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.OptionsRequestHandler;
import optionalrest.core.scope.Scope;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.mapping.AnnotationBundle;
import optionalrest.core.services.mapping.MethodRequestHandler;
import optionalrest.core.services.mapping.providers.Evaluator;
import optionalrest.core.services.mapping.providers.parameters.ParameterConverter;
import optionalrest.core.services.mapping.providers.parameters.ParameterConverterProvider;
import optionalrest.core.services.mapping.providers.responses.ResponseConverter;
import optionalrest.core.services.mapping.providers.responses.ResponseConverterProvider;
import optionalrest.core.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopeManager {

    private ScopeHelper scopeHelper = new ScopeHelper();
    private ScopeRegistry scopeRegistry;

    private Collection<ScopeContainer> containers;
    private Collection<ParameterConverterProvider> parameterConverterProviders;
    private Collection<ResponseConverterProvider> responseConverterProviders;

    private Map<Evaluator<Parameter>, ParameterConverter> parameterConverters = new HashMap<>();
    private Map<Evaluator<Object>, ResponseConverter> responseConverters = new HashMap<>();

    private OptionsRequestHandler optionsRequestHandler;

    public void initialize() {
        for (ParameterConverterProvider provider : parameterConverterProviders) {
            parameterConverters.putAll(provider.getConverters());
        }

        for (ResponseConverterProvider provider : responseConverterProviders) {
            responseConverters.putAll(provider.getConverters());
        }

        for (ScopeContainer scopeContainer : containers) {
            processContainer(scopeContainer);
        }
    }

    private void processContainer(ScopeContainer container) {

        Entity entity = ReflectionUtils.retrieveAnnotation(container.getClass(), Entity.class);
        if (entity != null) {
            String scopeId = scopeHelper.retrieveScopeId(container.getClass());
            if (StringUtils.isBlank(scopeId)) {
                throw new RestException("Cannot assign entity because no scopeId was found for " + container.getClass());
            }
            Scope scope = produceScope(container, scopeId);
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
        if (bundle.contains(Error.class)) {
            String scopeId = scopeHelper.retrieveScopeId(accessibleObject, container.getClass());
            Scope scope = produceScope(container, scopeId);
            if (accessibleObject instanceof Method) {
                Method method = (Method) accessibleObject;
                MethodRequestHandler requestHandler = new MethodRequestHandler(container, method);
                scope.addErrorRequestHandler(requestHandler);

                // scope helper provides defaults if necessary
                Handle handle = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Handle.class);
                scopeHelper.updateSupported(requestHandler, handle);
                requestHandler.responseTypeMappers(responseConverters).parameterMappers(parameterConverters);
            }
        }
    }

    private boolean checkInstanceRelative(ScopeContainer container, AccessibleObject accessibleObject, AnnotationBundle bundle) {
        Relative relative = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Relative.class);
        Instance instance = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Instance.class);
        if (relative == null || instance == null) {
            return false;
        }

        String relativePath = relative.path();

        String parentId = scopeHelper.retrieveScopeId(container.getClass());
        Scope parentScope = produceScope(container, parentId);

        String instanceId = scopeHelper.retrieveInstanceScopeId(accessibleObject, container.getClass());
        Scope instanceScope = produceScope(container, instanceId);
        parentScope.setInstanceScope(instanceScope);

        String relativeId = scopeHelper.retrieveRelativeScopeId(accessibleObject);
        Scope relativeScope = produceScope(null, relativeId);
        instanceScope.addRelativeScope(relativePath, relativeScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            addRequestHandler(accessibleObject, container, relativeScope, bundle);
        }

        return true;
    }

    private Scope produceScope(ScopeContainer container, String parentId) {
        return scopeRegistry.produceScope(parentId, container).optionsRequestHandler(optionsRequestHandler);
    }

    private boolean checkInstance(ScopeContainer container, AccessibleObject accessibleObject, AnnotationBundle bundle) {
        Instance instance = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Instance.class);
        if (instance == null) {
            return false;
        }

        String parentId = scopeHelper.retrieveScopeId(container.getClass());
        Scope parentScope = produceScope(container, parentId);

        String instanceId = scopeHelper.retrieveInstanceScopeId(accessibleObject, container.getClass());
        Scope instanceScope = produceScope(container, instanceId);
        parentScope.setInstanceScope(instanceScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            addRequestHandler(accessibleObject, container, instanceScope, bundle);
        }

        return true;
    }

    private boolean checkRelative(ScopeContainer container, AccessibleObject accessibleObject, AnnotationBundle bundle) {
        Relative relative = scopeHelper.retrieveAnnotation(accessibleObject, container.getClass(), Relative.class);
        if (relative == null) {
            return false;
        }

        String relativePath = relative.path();

        String parentId = scopeHelper.retrieveScopeId(container.getClass());
        Scope parentScope = produceScope(container, parentId);

        String relativeId = scopeHelper.retrieveRelativeScopeId(accessibleObject);
        Scope relativeScope = produceScope(null, relativeId);
        parentScope.addRelativeScope(relativePath, relativeScope);

        // only necessary for Method
        if (accessibleObject instanceof Method) {
            addRequestHandler(accessibleObject, container, relativeScope, bundle);
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

    private void addRequestHandler(AccessibleObject accessibleObject, ScopeContainer container, Scope scope, AnnotationBundle bundle) {
        List<Annotation> handles = bundle.subList(Handle.class);
        List<Annotation> orders = bundle.subList(Order.class);
        List<Annotation> befores = bundle.subList(Before.class);
        List<Annotation> afters = bundle.subList(After.class);

        if (accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;

            MethodRequestHandler requestHandler = new MethodRequestHandler(container, method);
            scope.addRequestHandler(requestHandler);

            requestHandler.passing(Void.TYPE.equals(method.getReturnType()));
            requestHandler.incrementOrder(Void.TYPE.equals(method.getReturnType()) ? 0 : 1);
            for (Annotation order : orders) {
                requestHandler.incrementOrder(((Order) order).value());
            }

            for (Annotation handle : handles) {
                scopeHelper.updateSupported(requestHandler, (Handle) handle);
            }

            for (Annotation before : befores) {
                for (Class<? extends ScopeContainer> beforeClass : ((Before)before).value()) {
                    String scopeId = scopeHelper.retrieveScopeId(beforeClass);
                    requestHandler.addBefore(produceScope(container, scopeId));
                }
            }

            for (Annotation after : afters) {
                for (Class<? extends ScopeContainer> afterClass : ((After)after).value()) {
                    String scopeId = scopeHelper.retrieveScopeId(afterClass);
                    requestHandler.addBefore(produceScope(container, scopeId));
                }
            }

            requestHandler.responseTypeMappers(responseConverters).parameterMappers(parameterConverters);
        }
    }

    private void checkForScope(AccessibleObject accessibleObject, ScopeContainer container, AnnotationBundle bundle) {
        String scopeId = getScopeId(accessibleObject, container);
        Scope scope = produceScope(container, scopeId);
        addRequestHandler(accessibleObject, container, scope, bundle);
    }

    private String getScopeId(AccessibleObject accessibleObject, ScopeContainer container) {
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

    public ScopeManager scopeHelper(ScopeHelper scopeHelper) {
        this.scopeHelper = scopeHelper;
        return this;
    }

    public ScopeManager scopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
        return this;
    }

    public ScopeManager containers(Collection<ScopeContainer> containers) {
        this.containers = containers;
        return this;
    }

    public ScopeManager parameterMapperProviders(Collection<ParameterConverterProvider> parameterConverterProviders) {
        this.parameterConverterProviders = parameterConverterProviders;
        return this;
    }

    public ScopeManager responseConverterProviders(Collection<ResponseConverterProvider> responseConverterProviders) {
        this.responseConverterProviders = responseConverterProviders;
        return this;
    }

    public ScopeManager optionsRequestHandler(OptionsRequestHandler optionsRequestHandler) {
        this.optionsRequestHandler = optionsRequestHandler;
        return this;
    }
}
