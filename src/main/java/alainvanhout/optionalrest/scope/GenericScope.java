package alainvanhout.optionalrest.scope;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.response.RendererResponse;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.BuildParameters;
import alainvanhout.optionalrest.scope.definition.ScopeDefinition;
import alainvanhout.optionalrest.services.mapping.Mapping;
import alainvanhout.optionalrest.services.mapping.Mappings;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.optionalrest.utils.RestUtils;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.LinkRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

import static alainvanhout.optionalrest.request.meta.HttpMethod.OPTIONS;

public class GenericScope extends BasicScope {

    private transient ScopeDefinition definition = new ScopeDefinition();

    private transient Mappings passMappings = new Mappings();
    private transient Mappings arriveMappings = new Mappings();
    private transient Mappings errorMappings = new Mappings();

    private transient Scope instanceScope;

    @Override
    public Response follow(Request request) {
        try {
            // always call passing scope
            Mapping passMapping = passMappings.getMapping(request);
            if (passMapping != null) {
                call(passMapping, request);
            }

            // arriving at scope
            if (request.getPath().isDone()) {
                Mapping arriveMapping = arriveMappings.getMapping(request);
                if (arriveMapping != null) {
                    return call(arriveMapping, request);
                } else if (OPTIONS.equals(request.getMethod())) {
                    int deep = request.getParameters().getIntValue("deep", 1);
                    BuildParameters buildParameters = new BuildParameters()
                            .includeScopeId(request.getParameters().contains("SCOPE_ID"))
                            .asHtml(request.getHeaders().contains("accept", "text/html"));
                    Map<String, Object> definitionMap = buildDefinitionMap(deep, buildParameters);

                    if (buildParameters.getAsHtml()) {
                        String json = JsonUtils.definitionToJson(definitionMap);

                        return new RendererResponse().renderer(new PreRenderer(json));
                    } else {
                        String json = JsonUtils.definitionToJson(definitionMap);
                        return new RendererResponse().renderer(new StringRenderer(json));
                    }
                }
                throw new RestException("No arrival mapping available");
            }

            // not yet arrived
            String step = request.getPath().nextStep();

            // first check relative scopes
            if (relativeScopes.containsKey(step)) {
                Response call = call(relativeScopes.get(step), request);
                return call;
            }

            // and finally check fallback scope
            if (instanceScope != null) {
                return instanceScope.follow(request);
            }

        } catch (Exception e) {
            Mapping errorMapping = errorMappings.getMapping(request);
            if (errorMapping != null) {
                request.addToContext("exception", e);
                return call(errorMapping, request);
            }
            throw e;
        }

        throw new RestException("No appropriate mapping found for scope " + this.getClass().getSimpleName());
    }

    @Override
    public Map<String, Object> buildDefinitionMap(int deep, BuildParameters params) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (params.getIncludeScopeId()) {
            conditionalAdd(map, "id", scopeId);
        }
        ;
        conditionalAdd(map, "name", definition.getName());
        conditionalAdd(map, "description", definition.getDescription());
        conditionalAdd(map, "type", definition.getType());

        Set<HttpMethod> methods = arriveMappings.supported(HttpMethod.class.getName());
        if (methods.size() > 0) {
            methods.add(OPTIONS);
            map.put("methods", methods);
        }

        if (definition.getInternalClass() != null) {
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
                map.put("instance", instanceScope.buildDefinitionMap(deep - 1, params));
            }
            if (relativeScopes.size() > 0) {
                Map<String, Object> relativeMap = new LinkedHashMap<>();
                for (Map.Entry<String, Scope> entry : relativeScopes.entrySet()) {
                    String key = entry.getKey();
                    Map<String, Object> value = entry.getValue().buildDefinitionMap(deep - 1, params);
                    if (params.getAsHtml()) {
                        relativeMap.put(new LinkRenderer().href(key + "/?OPTIONS").add(key).render(), value);
                    } else {
                        relativeMap.put(key, value);
                    }
                }
                map.put("relative", relativeMap);
            }
        }
        return map;
    }

    public void conditionalAdd(Map<String, Object> map, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }

    private Response call(Scope scope, Request request) {
        return scope.follow(request);
    }

    private Response call(Mapping mapping, Request request) {
        try {
            return mapping.call(request);
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

    private GenericScope addMapping(Mappings mappings, Mapping mapping, HttpMethod... methods) {
        mapping.supportAll(HttpMethod.class.getName(), Arrays.asList(methods));
        mappings.add(mapping);
        return this;
    }

    @Override
    public void setInstanceScope(Scope scope) {
        this.instanceScope = scope;
    }

    @Override
    public String toString() {
        return definition.toString();
    }

    @Override
    public GenericScope scopeId(String scopeId) {
        this.scopeId = scopeId;
        return this;
    }

    @Override
    public String getScopeId() {
        return scopeId;
    }
}
