package alainvanhout.rest.scope;

import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.restservice.RestMapping;
import alainvanhout.rest.restservice.RestMappings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BasicScope implements Scope {

    private RestMappings passMappings = new RestMappings();
    private RestMappings arriveMappings = new RestMappings();
    private RestMappings fallbackMappings = new RestMappings();
    private RestMappings errorMappings = new RestMappings();
    private Map<String, Scope> relativeScopes = new HashMap<>();
    private Scope fallbackScope;

    @Override
    public RestResponse follow(RestRequest restRequest) {
        try {
            // always call passing scope
            if (passMappings.contains(restRequest.getMethod())) {
                call(passMappings, restRequest);
            }

            // arriving at scope
            if (restRequest.getPath().isDone() && arriveMappings.contains(restRequest.getMethod())) {
                return call(arriveMappings, restRequest);
            }

            // not yet arrived
            String step = restRequest.getPath().nextStep();

            // first check relative scopes
            if (relativeScopes.containsKey(step)) {
                return relativeScopes.get(step).follow(restRequest);
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
                    Field field = mapping.getOwner().getClass().getDeclaredField(mapping.getAccessibleName());
                    field.setAccessible(true);
                    Scope target = (Scope) field.get(mapping.getOwner());
                    return target.follow(restRequest);
                case METHOD:
                    Method method = mapping.getOwner().getClass().getDeclaredMethod(mapping.getAccessibleName(), RestRequest.class);
                    return (RestResponse) method.invoke(mapping.getOwner(), restRequest);
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
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException e) {
            throw new RestException("Unable to call mapping", e).add("mapping", mapping);
        }
    }

    @Override
    public BasicScope addPassMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(passMappings, mapping, methods);
    }

    @Override
    public BasicScope addArriveMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(arriveMappings, mapping, methods);
    }

    @Override
    public BasicScope addFallbackMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(fallbackMappings, mapping, methods);
    }

    @Override
    public BasicScope addErrorMapping(RestMapping mapping, HttpMethod... methods) {
        return addMapping(errorMappings, mapping, methods);
    }

    private BasicScope addMapping(RestMappings mappings, RestMapping mapping, HttpMethod... methods) {
        for (HttpMethod method : methods) {
            mappings.addMapping(method, mapping);
        }
        return this;
    }

    public BasicScope addRelativeScope(String relative, Scope scope) {
        if (relativeScopes.containsKey(relative)) {
            throw new RestException("Relative scope already set: " + relative);
        }
        relativeScopes.put(relative, scope);
        return this;
    }

    public Scope getRelativeScope(String relative, boolean create) {
        if (create && !relativeScopes.containsKey(relative)) {
            addRelativeScope(relative, new BasicScope());
        }
        if (relativeScopes.containsKey(relative)) {
            return relativeScopes.get(relative);
        }
        throw new RestException("Relative scope not defined: " + relative);
    }

    public void setFallbackScope(Scope fallbackScope) {
        this.fallbackScope = fallbackScope;
    }
}
