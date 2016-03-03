package alainvanhout.demo.scopes;

import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.annotations.instance.RestInstance;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.GenericScope;
import alainvanhout.optionalrest.scope.Scope;
import alainvanhout.optionalrest.scope.ScopeDto;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeRegistry;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.LinkRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
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

    @RestInstance
    public Renderer instance(Request request) {
        String step = request.getPath().getStep();
        Scope scope = scopeRegistry.findByName(step);
        return new PreRenderer(JsonUtils.objectToUnescapedJson(toDto(scope)));
    }

    @RestEntity
    public Renderer arrive(Request request) {
        Collection<Scope> scopes = scopeRegistry.findAll();
        List<ScopeDto> dtos = scopes.stream().map(this::toDto).collect(Collectors.toList());
        return new PreRenderer(JsonUtils.objectToUnescapedJson(dtos));
    }

    public ScopeDto toDto(Scope scope) {
        Map<String, String> relativeScopes = scope.getRelativeScopes().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> toLink(e.getValue())));

        ScopeDto dto = new ScopeDto()
                .id(toLink(scope))
                .relative(relativeScopes);
        return dto;
    }

    public String toLink(Scope scope) {
        return new LinkRenderer().href(scope.getScopeId()).add(scope.getScopeId()).render();
    }
}
