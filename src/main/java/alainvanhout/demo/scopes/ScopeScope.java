package alainvanhout.demo.scopes;

import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.BasicScope;
import alainvanhout.optionalrest.scope.GenericScope;
import alainvanhout.optionalrest.scope.Scope;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeRegistry;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ScopeDefinition(name = "scope")
@EntityDefinition(instanceClass = GenericScope.class)
public class ScopeScope implements ScopeContainer {

    @Autowired
    private ScopeRegistry scopeRegistry;

    @RestEntity
    public Renderer arrive(Request request) {
        Collection<Scope> scopes = scopeRegistry.findAll();

        List<BasicScope> basicScopes = scopes.stream().map(s -> {
            BasicScope scope = new BasicScope();
            scope.scopeId(s.getScopeId());
            return scope;
        }).collect(Collectors.toList());

        PreRenderer preRenderer = new PreRenderer(JsonUtils.objectToJson(scopes));
        return preRenderer;
    }
}
