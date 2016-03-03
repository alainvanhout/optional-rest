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
import alainvanhout.optionalrest.response.RendererResponse;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.ContextRenderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.select.OptionRenderer;
import alainvanhout.renderering.renderer.list.GenericListRenderer;
import alainvanhout.renderering.renderer.list.ListRenderer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ScopeDefinition(name = "template")
@EntityDefinition(instanceClass = Template.class)
public class TemplateScope implements ScopeContainer {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateRepository templateRepository;

    @RestInstance(methods = {HttpMethod.POST})
    public Response idArrivePost(Request request) {
        String id = request.getPath().getStep();
        Template template = templateRepository.findByName(id);

        Parameters parameters = request.getParameters();
        String templateBody = parameters.getValue("templateBody");
        templateBody = StringUtils.replace(templateBody, "{textarea", "<textarea");
        templateBody = StringUtils.replace(templateBody, "textarea}", "textarea>");
        template.setBody(templateBody);
        template.setName(parameters.getValue("templateId"));
        templateRepository.save(template);

        return new RendererResponse().redirectUrl(request.getQuery());
    }

    @RestInstance(methods = {HttpMethod.GET})
    public Renderer idArrive(Request request) {
        String id = request.getPath().getStep();
        Template template = templateRepository.findByName(id);

        String templateBody = template.getBody();
        templateBody = StringUtils.replace(templateBody, "<textarea", "{textarea");
        templateBody = StringUtils.replace(templateBody, "textarea>", "textarea}");

        ContextRenderer form = new SimpleContextRenderer(templateService.findBodyAsRenderer("edit-templates"));
        ListRenderer renderer = new GenericListRenderer<Template>()
                .preProcess(t -> {
                            OptionRenderer option = new OptionRenderer();
                            if (t.getName().equals(id)){
                                option.attribute("selected", "selected");
                            }
                            return option.add(t.getName());
                        }
                )
                .addAll(templateRepository.findAll());

        if (id != null && StringUtils.isNotBlank(templateBody)) {
            form.set("templateId", id);
            form.set("templateBody", templateBody);
            form.set("templateList", renderer);
        } else {
            form.set("templateId", "");
            form.set("templateBody", "");
            form.set("templateList", renderer);
        }

        return form;
    }

    @RestEntity
    public Renderer arrive(Request request) {
        return new PreRenderer(JsonUtils.objectToJson(templateRepository.findAll()));
    }
}
