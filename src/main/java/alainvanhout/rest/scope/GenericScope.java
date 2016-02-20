package alainvanhout.rest.scope;

import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.LinkRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.request.meta.HttpMethod;
import alainvanhout.rest.services.mapping.Mapping;
import alainvanhout.rest.services.mapping.RestMappings;
import alainvanhout.rest.utils.JsonUtils;
import alainvanhout.rest.utils.RestUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenericScope implements Scope {

    private ScopeDefinition definition = new ScopeDefinition();

    private RestMappings passMappings = new RestMappings();
    private RestMappings arriveMappings = new RestMappings();
    private RestMappings errorMappings = new RestMappings();

    private Scope instanceScope;
    private Map<String, Scope> relativeScopes = new HashMap<>();

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
                    int deep = Integer.parseInt(StringUtils.defaultString(restRequest.getParameters().getValue("deep"), "1"));
                    Map<String, Object> definitionMap = buildDefinitionMap(deep);

                    if (restRequest.getHeaders().contains("accept", "text/html")) {
                        if (definitionMap.containsKey("relative")) {
                            Map<String, Object> relative = (Map) definitionMap.get("relative");
                            Map<String, Object> map = new LinkedHashMap<>();
                            for (String key : relative.keySet()) {
                                map.put(new LinkRenderer().href(key + "/?OPTIONS").add(key).render(), relative.get(key));
                            }
                            definitionMap.replace("relative", map);
                        }
                        String json = JsonUtils.definitionToJson(definitionMap);

                        return new RestResponse().renderer(new PreRenderer(json));
                    } else {
                        String json = JsonUtils.definitionToJson(definitionMap);
                        return new RestResponse().renderer(new StringRenderer(json));
                    }
                }
                throw new RestException("No arrival mapping available");
            }

            // not yet arrived
            String step = restRequest.getPath().nextStep();

            // first check relative scopes
            if (relativeScopes.containsKey(step)) {
                return call(relativeScopes.get(step), restRequest);
            }

            // and finally check fallback scope
            if (instanceScope != null) {
                return instanceScope.follow(restRequest);
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
    public Map<String,Object> buildDefinitionMap(int deep) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(definition.getName())) {
            map.put("name", definition.getName());
        }
        if (StringUtils.isNotBlank(definition.getType())) {
            map.put("type", definition.getType());
        }

        if (definition.getInternalClass() != null){
            Map<String, Object> internalMap = new LinkedHashMap<>();
            Field[] fields = definition.getInternalClass().getDeclaredFields();
            for (Field field : fields) {
                String type = RestUtils.typeOfField(field);
                if (type != null) {
                    internalMap.put(field.getName(), type);
                }
            }
            map.put("internal", internalMap);
        }

        if (deep > 0) {
            if (instanceScope != null) {
                map.put("instance", instanceScope.buildDefinitionMap(deep - 1));
            }
            if (relativeScopes.size() > 0) {
                Map<String, Object> relativeMap = new LinkedHashMap<>();
                for (Map.Entry<String, Scope> entry : relativeScopes.entrySet()) {
                    relativeMap.put(entry.getKey(), entry.getValue().buildDefinitionMap(deep - 1));
                }
                map.put("relative", relativeMap);
            }
        }
        return map;
    }

    private RestResponse call(Scope scope, RestRequest restRequest) {
        return scope.follow(restRequest);
    }

    private RestResponse call(RestMappings mappings, RestRequest restRequest) {
        return call(mappings.get(restRequest.getMethod()), restRequest);
    }

    private RestResponse call(Mapping mapping, RestRequest restRequest) {
        try {
            return mapping.call(restRequest);
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
    public GenericScope addPassMapping(Mapping mapping, HttpMethod... methods) {
        return addMapping(passMappings, mapping, methods);
    }

    @Override
    public GenericScope addArriveMapping(Mapping mapping, HttpMethod... methods) {
        return addMapping(arriveMappings, mapping, methods);
    }

    @Override
    public GenericScope addErrorMapping(Mapping mapping, HttpMethod... methods) {
        return addMapping(errorMappings, mapping, methods);
    }

    private GenericScope addMapping(RestMappings mappings, Mapping mapping, HttpMethod... methods) {
        for (HttpMethod method : methods) {
            mappings.addMapping(method, mapping);
        }
        return this;
    }

    @Override
    public void setInstanceScope(Scope scope) {
        this.instanceScope = scope;
    }

    @Override
    public void addRelativeScope(String relative, Scope scope) {
        relativeScopes.put(relative, scope);
    }

    @Override
    public String toString() {
        return definition.toString();
    }

}
