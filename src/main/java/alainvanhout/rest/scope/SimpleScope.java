package alainvanhout.rest.scope;

import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.services.RestMapping;
import alainvanhout.rest.services.RestMappings;
import alainvanhout.rest.services.ScopeManager;
import alainvanhout.rest.utils.JsonUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SimpleScope implements BasicScope {

    private ScopeManager scopeManager;

    private RestMappings passMappings = new RestMappings();
    private RestMappings arriveMappings = new RestMappings();
    private RestMappings fallbackMappings = new RestMappings();
    private RestMappings errorMappings = new RestMappings();
    private Map<String, RestMapping> relativeMapping = new HashMap<>();
    private Scope fallbackScope;
    private Class entityClass;

    public SimpleScope(ScopeManager scopeManager) {
        this.scopeManager = scopeManager;
    }

    @Override
    public RestResponse follow(RestRequest restRequest) {
        try {
            // always call passing scope
            if (passMappings.contains(restRequest.getMethod())) {
                call(passMappings, restRequest);
            }

            // arriving at scope
            if (restRequest.getPath().isDone()) {
                if (arriveMappings.contains(restRequest.getMethod())) {
                    return call(arriveMappings, restRequest);
                } else if (HttpMethod.OPTIONS.equals(restRequest.getMethod()) && entityClass != null) {
                    Map<String, Map> definitionMap = JsonUtils.getDefinitionMap(entityClass);
                    String json = JsonUtils.definitionToJson(definitionMap);
                    return new RestResponse().renderer(new StringRenderer(json));
                }
                throw new RestException("No arrival mapping available");
            }

            // not yet arrived
            String step = restRequest.getPath().nextStep();

            // first check relative scopes
            if (relativeMapping.containsKey(step)) {
                return call(relativeMapping.get(step), restRequest);
            }

            // then check fallback mapping
            // (this takes priority over fallback scope because the latter is inherently a catch-all)
            if (fallbackMappings.contains(restRequest.getMethod())) {
                return call(fallbackMappings, restRequest);
            }

            // and finally check fallback scope
            if (fallbackScope != null) {
                return fallbackScope.follow(restRequest);
            }

        } catch (Exception e) {
            if (errorMappings.contains(restRequest.getMethod())) {
                restRequest.addToContext("exception", e);
                return call(errorMappings, restRequest);
            }
            throw e;
        }

        throw new RestException("No appropriate mapping found for scope " + this.getClass().getSimpleName());
    }

    private RestResponse call(RestMappings mappings, RestRequest restRequest) {
        return call(mappings.get(restRequest.getMethod()), restRequest);
    }

    private RestResponse call(RestMapping mapping, RestRequest restRequest) {
        try {
            switch (mapping.getType()) {
                case FIELD:
                    Field field = mapping.getField();
                    field.setAccessible(true);
                    Scope target = (Scope) field.get(mapping.getOwner());
                    return target.follow(restRequest);
                case METHOD:
                    Method method = mapping.getMethod();
                    return (RestResponse) method.invoke(mapping.getOwner(), restRequest);
                case SCOPE_CONTAINER:
                    ScopeContainer container = mapping.getScopeContainer();
                    return scopeManager.getScopeForContainer(container).follow(restRequest);
                default:
                    throw new RestException("Unsupported type:" + mapping.getType());
            }
        } catch (RestException e) {
            e.add("mapping", mapping);
            throw e;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RestException) {
                throw ((RestException) e.getCause()).add("mapping", mapping);
            }
            throw new RestException("Unable to call mapping", e).add("mapping", mapping);
        } catch (IllegalAccessException e) {
            throw new RestException("Unable to call mapping", e).add("mapping", mapping);
        }
    }

    @Override
    public SimpleScope addPassMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(passMappings, mapping, methods);
    }

    @Override
    public SimpleScope addArriveMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(arriveMappings, mapping, methods);
    }

    @Override
    public SimpleScope addFallbackMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(fallbackMappings, mapping, methods);
    }

    @Override
    public SimpleScope addErrorMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(errorMappings, mapping, methods);
    }

    private SimpleScope addMapping(RestMappings mappings, RestMapping mapping, HttpMethod... methods) {
        for (HttpMethod method : methods) {
            mappings.addMapping(method, mapping);
        }
        return this;
    }

    @Override
    public SimpleScope addRelativeMapping(String relative, RestMapping mapping) {
        if (relativeMapping.containsKey(relative)) {
            throw new RestException("Relative mappin already defined: " + relative);
        }
        relativeMapping.put(relative, mapping);
        return this;
    }

    public void setFallbackScope(Scope fallbackScope) {
        this.fallbackScope = fallbackScope;
    }

    public void setDefinitionClass(Class entityClass) {
        this.entityClass = entityClass;
    }
}
