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
import alainvanhout.rest.utils.RestUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleScope implements BasicScope {

    private ScopeManager scopeManager;
    private String type;
    // entity
    private Class entityClass;
    // primitive and relative mappings
    private RestMappings passMappings = new RestMappings();
    private RestMappings arriveMappings = new RestMappings();
    private Map<String, RestMapping> relativeMapping = new HashMap<>();
    // error mappings
    private RestMappings errorMappings = new RestMappings();
    // fallback mappings
    private BasicScope fallbackScope;
    private RestMappings fallbackMappings = new RestMappings();

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
                } else if (HttpMethod.OPTIONS.equals(restRequest.getMethod())) {
                    Map<String, Object> definitionMap = getDefinitionMap();
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

    @Override
    public Map<String, Object> getDefinitionMap() {
        Map<String, Object> definitionMap = new LinkedHashMap<>();
        Map<String, Object> internalMap = getInternalsMap();
        Map<String, Object> relativeMap = getAsscociatedMap();

        if (StringUtils.isNotBlank(type)) {
            definitionMap.put("type", type);
        }
        if (internalMap.size() > 0) {
            definitionMap.put("internal", internalMap);
        }
        if (fallbackScope != null && StringUtils.isNotBlank(fallbackScope.getType())){
            definitionMap.put(fallbackScope.getType(), fallbackScope.getDefinitionMap());
        }
        if (relativeMap.size() > 0) {
            definitionMap.put("relative", relativeMap);
        }
        return definitionMap;
    }

    private Map<String, Object> getAsscociatedMap() {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, RestMapping> relative : relativeMapping.entrySet()) {
            RestMapping mapping = relative.getValue();
            if (mapping.getType().equals(RestMapping.RestMappingType.SCOPE_CONTAINER)) {
                ScopeContainer container = mapping.getScopeContainer();
                BasicScope scope = scopeManager.getScopeForContainer(container);
            }
            map.put(relative.getKey(), "");
        }
        return map;
    }

    public Map<String, Object> getInternalsMap() {
        Map<String, Object> map = new HashMap<>();
        if (entityClass != null) {
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                String type = RestUtils.typeOfField(field);
                if (type != null) {
                    map.put(field.getName(), type);
                }
            }
        }
        return map;
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
            throw new RestException("Relative mapping already defined: " + relative);
        }
        relativeMapping.put(relative, mapping);
        return this;
    }

    public void setFallbackScope(BasicScope fallbackScope) {
        this.fallbackScope = fallbackScope;
    }

    public void setDefinitionClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String getType() {
        return type;
    }

    public SimpleScope type(String type) {
        this.type = type;
        return this;
    }
}
