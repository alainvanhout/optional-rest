package optionalrest.core.scope;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.rendering.RendererResponse;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.BuildParameters;
import optionalrest.core.scope.definition.ScopeDefinition;
import optionalrest.core.services.mapping.Mapping;
import optionalrest.core.services.mapping.Mappings;
import optionalrest.core.utils.JsonUtils;
import optionalrest.core.utils.RestUtils;
import renderering.core.basic.StringRenderer;
import renderering.web.html.basic.documentbody.LinkRenderer;
import renderering.web.html.basic.documentbody.PreRenderer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static optionalrest.core.request.meta.HttpMethod.OPTIONS;

public class GenericScope extends BasicScope {

    private transient ScopeDefinition definition = new ScopeDefinition();

    private transient Mappings passMappings = new Mappings();
    private transient Mappings errorMappings = new Mappings();

    private transient Scope instanceScope;

    @Override
    public Response follow(Request request) {
        try {
            // always run passing mappings (some may involve setting the response)
            pass(request);

            // whether arrived or not, if request is done, return response
            if (request.isDone()){
                return request.getResponse();
            }

            // arriving at scope
            if (request.getPath().isArrived()) {
                return arrive(request);
            }

            // continue with request
            return proceed(request);

        } catch (Exception e) {
            List<Mapping> errorMapping = errorMappings.getMappings();
            if (!errorMapping.isEmpty()) {
                request.getContext().add("exception", e);
                return apply(errorMapping, request);
            }
            throw e;
        }

    }

    @Override
    public void pass(Request request) {
        List<Mapping> passing = passMappings.getMappings(request, true);
        apply(passing, request);
    }

    @Override
    public Response arrive(Request request) {
        // run arrive mappings
        List<Mapping> arriving = passMappings.getMappings(request, false);
        apply(arriving, request);

        // arrived with response
        if (request.hasResponse()) {
            return request.getResponse();
        }

        // default OPTIONS response
        if (OPTIONS.equals(request.getMethod())) {
            return createDefaultOptionsResponse(request);
        }

        throw new RestException("No response was set");
    }

    public Response proceed(Request request) {
        String step = request.getPath().nextStep();

        // first check relative scopes
        if (relativeScopes.containsKey(step)) {
            return apply(relativeScopes.get(step), request);
        }

        // then check fallback scope
        if (instanceScope != null) {
            return instanceScope.follow(request);
        }

        throw new RestException("No appropriate mapping found for scope " + this.getClass().getSimpleName());
    }

    public Response createDefaultOptionsResponse(Request request) {
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

    @Override
    public Map<String, Object> buildDefinitionMap(int deep, BuildParameters params) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (params.getIncludeScopeId()) {
            conditionalAdd(map, "id", scopeId);
        }

        conditionalAdd(map, "name", definition.getName());
        conditionalAdd(map, "description", definition.getDescription());
        conditionalAdd(map, "type", definition.getType());

        Supported supported = passMappings.supported();
        if (supported.getMethods().size() > 0) {
            supported.getMethods().add(OPTIONS);
            map.put("methods", supported.getMethods());
        }
        if (supported.getAccept().size() > 0) {
            map.put("accept", supported.getAccept());
        }
        if (supported.getContentType().size() > 0) {
            map.put("contentType", supported.getContentType());
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
            map.put("fields", internalMap);
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
                        relativeMap.put(new LinkRenderer().href(key + "/?" + OPTIONS).add(key).render(), value);
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

    private Response apply(Scope scope, Request request) {
        return scope.follow(request);
    }

    private Response apply(Collection<Mapping> mappings, Request request) {
        for (Mapping mapping : mappings) {
            apply(mapping, request);
            if (request.isDone()) {
                break;
            }
        }
        return request.getResponse();
    }

    private void apply(Mapping mapping, Request request) {
        try {
            for (Scope scope : mapping.getBefore()) {
                scope.pass(request);
            }

            mapping.apply(request);

            for (Scope scope : mapping.getAfter()) {
                scope.pass(request);
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
    public GenericScope addPassMapping(Mapping mapping) {
        return addMapping(passMappings, mapping);
    }

    @Override
    public GenericScope addErrorMapping(Mapping mapping) {
        return addMapping(errorMappings, mapping);
    }

    private GenericScope addMapping(Mappings mappings, Mapping mapping) {
        mappings.add(mapping);
        return this;
    }

    @Override
    public void setInstanceScope(Scope scope) {
        this.instanceScope = scope;
    }

    @Override
    public String toString() {
        return scopeId;
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
