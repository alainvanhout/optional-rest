package alainvanhout.demo.scopes;

import optionalrest.core.annotations.EntityDefinition;
import optionalrest.core.annotations.ScopeDefinition;
import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.annotations.scopes.Instance;
import optionalrest.core.request.Request;
import optionalrest.core.scope.GenericScope;
import optionalrest.core.scope.Scope;
import optionalrest.core.scope.ScopeDto;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.ScopeRegistry;
import optionalrest.core.services.factories.Step;
import optionalrest.rendering.JsonUtils;
import renderering.core.Renderer;
import renderering.web.html.basic.documentbody.LinkRenderer;
import renderering.web.html.basic.documentbody.PreRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ScopeDefinition(name = "scope")
@EntityDefinition(instanceClass = GenericScope.class)
public class ScopeScope implements ScopeContainer {

    @Autowired
    private ScopeRegistry scopeRegistry;

    @Instance @Get
    public Renderer instance(Request request, @Step String id) {
        Scope scope = scopeRegistry.findByName(id);
        return new PreRenderer(JsonUtils.objectToUnescapedJson(toDto(scope)));
    }

    @Get
    public Renderer arrive(Request request) {
        Collection<Scope> scopes = scopeRegistry.findAll();
        List<ScopeDto> dtos = scopes.stream().map(this::toDto).collect(Collectors.toList());
        return new PreRenderer(JsonUtils.objectToUnescapedJson(dtos));
    }

    private ScopeDto toDto(Scope scope) {
        Map<String, String> relativeScopes = scope.getRelativeScopes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toLink(e.getValue())));

        return new ScopeDto()
                .id(toLink(scope))
                .relative(relativeScopes);
    }

    private String toLink(Scope scope) {
        return new LinkRenderer().href(scope.getScopeId()).add(scope.getScopeId()).render();
    }
}
