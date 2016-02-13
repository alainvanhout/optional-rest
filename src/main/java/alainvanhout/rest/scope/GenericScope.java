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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GenericScope implements Scope {

    private ScopeManager scopeManager;
    private ScopeDefinition definition = new ScopeDefinition();
    // primitive and relative mappings
    private RestMappings passMappings = new RestMappings();
    private RestMappings arriveMappings = new RestMappings();
    private Map<String, RestMapping> relativeMapping = new HashMap<>();
    // error mappings
    private RestMappings errorMappings = new RestMappings();
    // fallback mappings
    private Scope fallbackScope;
    private RestMappings fallbackMappings = new RestMappings();

    private Map<String, Supplier<Scope>> relativeScopes = new HashMap<>();

    public GenericScope(ScopeManager scopeManager) {
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
                } else if (HttpMethod.OPTIONS.equals(restRequest.getMethod())) {
                    Map<String, Object> definitionMap = definition.getMap();
                    String json = JsonUtils.definitionToJson(definitionMap);
                    return new RestResponse().renderer(new StringRenderer(json));
                }
                throw new RestException("No arrival mapping available");
            }

            // not yet arrived
            String step = restRequest.getPath().nextStep();

            // first check relative scopes
            if (relativeScopes.containsKey(step)) {
                return call(relativeScopes.get(step), restRequest);
            }

            // then check relative mappings
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

    private RestResponse call(Supplier<Scope> scopeSupplier, RestRequest restRequest) {
        return scopeSupplier.get().follow(restRequest);
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
                    ScopeContainer target = (ScopeContainer) field.get(mapping.getOwner());
                    Scope scope = scopeManager.getScopeForContainer(target);
                    return scope.follow(restRequest);
                case METHOD:
                    Method method = mapping.getMethod();
                    method.setAccessible(true);
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
        } catch (Exception e) {
            if (e.getCause() instanceof RestException) {
                throw ((RestException) e.getCause()).add("mapping", mapping);
            }
            throw new RestException("Unable to call mapping", e).add("mapping", mapping);
        }
    }

    @Override
    public ScopeDefinition getDefinition() {
        return definition;
    }

    @Override
    public GenericScope addPassMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(passMappings, mapping, methods);
    }

    @Override
    public GenericScope addArriveMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(arriveMappings, mapping, methods);
    }

    @Override
    public GenericScope addFallbackMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(fallbackMappings, mapping, methods);
    }

    @Override
    public GenericScope addErrorMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(errorMappings, mapping, methods);
    }

    private GenericScope addMapping(RestMappings mappings, RestMapping mapping, HttpMethod... methods) {
        for (HttpMethod method : methods) {
            mappings.addMapping(method, mapping);
        }
        return this;
    }

    @Override
    public void addRelativeMapping(String relative, RestMapping mapping) {
        if (relativeMapping.containsKey(relative)) {
            throw new RestException("Relative mapping already defined: " + relative);
        }
        relativeMapping.put(relative, mapping);
    }

    public void setFallbackScope(Scope fallbackScope) {
        this.fallbackScope = fallbackScope;
    }

    public GenericScope addRelativeScope(String relative, Supplier<Scope> supplier){
        relativeScopes.put(relative, supplier);
        return this;
    }

    public Scope getRelativeScope(String relative){
        if (!relativeScopes.containsKey(relative)) {
            throw new RestException("Relative scope not available: " + relative);
        }
        return relativeScopes.get(relative).get();
    }
}
