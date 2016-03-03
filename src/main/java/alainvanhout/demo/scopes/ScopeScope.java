package alainvanhout.demo.scopes;

import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.cms.services.TemplateService;
import alainvanhout.demo.Template;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.annotations.instance.RestInstance;
import alainvanhout.optionalrest.request.Parameters;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.scope.GenericScope;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeManager;
import alainvanhout.optionalrest.services.ScopeRegistry;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.ContextRenderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.LinkRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.select.OptionRenderer;
import alainvanhout.renderering.renderer.list.GenericListRenderer;
import alainvanhout.renderering.renderer.list.ListRenderer;
import alainvanhout.renderering.renderer.retrieve.TextResourceRenderer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ScopeDefinition(name = "scope")
@EntityDefinition(instanceClass = GenericScope.class)
public class ScopeScope implements ScopeContainer {

    @Autowired
    private ScopeRegistry scopeRegistry;

    @RestEntity
    public Renderer arrive(Request request) {
        return new PreRenderer(JsonUtils.objectToJson(scopeRegistry.findAll()));
    }
}
