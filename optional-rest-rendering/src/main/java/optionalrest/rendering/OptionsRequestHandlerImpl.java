package optionalrest.rendering;

import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.OptionsRequestHandler;
import optionalrest.core.scope.Scope;
import optionalrest.core.scope.Supported;
import optionalrest.core.scope.definition.BuildParameters;
import optionalrest.core.scope.definition.ScopeDefinition;
import optionalrest.core.utils.RestUtils;
import org.apache.commons.lang3.StringUtils;
import renderering.core.basic.StringRenderer;
import renderering.web.html.basic.documentbody.LinkRenderer;
import renderering.web.html.basic.documentbody.PreRenderer;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static optionalrest.core.request.meta.HttpMethod.OPTIONS;

public class OptionsRequestHandlerImpl implements OptionsRequestHandler {

    @Override
    public Response get(Request request, Scope scope) {
        int deep = request.getParameters().getIntValue("deep", 1);
        BuildParameters buildParameters = new BuildParameters()
                .includeScopeId(request.getParameters().contains("SCOPE_ID"))
                .asHtml(request.getHeaders().contains("accept", "text/html"));
        Map<String, Object> definitionMap = buildDefinitionMap(deep, buildParameters, scope);

        if (buildParameters.getAsHtml()) {
            String json = JsonUtils.definitionToJson(definitionMap);
            return new RendererResponse().renderer(new PreRenderer(json));
        } else {
            String json = JsonUtils.definitionToJson(definitionMap);
            return new RendererResponse().renderer(new StringRenderer(json));
        }
    }

    private Map<String, Object> buildDefinitionMap(int deep, BuildParameters params, Scope scope) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (params.getIncludeScopeId()) {
            conditionalAdd(map, "id", scope.getScopeId());
        }

        ScopeDefinition definition = scope.getDefinition();

        conditionalAdd(map, "name", definition.getName());
        conditionalAdd(map, "description", definition.getDescription());
        conditionalAdd(map, "type", definition.getType());

        Supported supported = scope.getSupported();
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

        Scope instanceScope = scope.getInstanceScope();
        Map<String, Scope> relativeScopes = scope.getRelativeScopes();

        if (deep > 0) {
            if (instanceScope != null) {
                map.put("instance", buildDefinitionMap(deep - 1, params, instanceScope));
            }
            if (relativeScopes.size() > 0) {
                Map<String, Object> relativeMap = new LinkedHashMap<>();
                for (Map.Entry<String, Scope> entry : relativeScopes.entrySet()) {
                    String key = entry.getKey();
                    Map<String, Object> value = buildDefinitionMap(deep - 1, params, entry.getValue());
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

    private void conditionalAdd(Map<String, Object> map, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }
}
