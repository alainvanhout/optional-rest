package alainvanhout.demo.scopes;

import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.cms.services.TemplateService;
import alainvanhout.demo.Template;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.Handle;
import alainvanhout.optionalrest.annotations.Instance;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.request.Parameters;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.response.RedirectResponse;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.factories.Step;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.ContextRenderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.select.OptionRenderer;
import alainvanhout.renderering.renderer.list.GenericListRenderer;
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

    @Handle
    public Renderer arrive(Request request) {
        return new PreRenderer(JsonUtils.objectToJson(templateRepository.findAll()));
    }

    @Instance
    @Handle(methods = {HttpMethod.GET})
    public Renderer idArrive(Request request, @Step String id) {
        Template template = templateRepository.findByName(id);

        String templateBody = template.getBody();
        templateBody = encodeElement(templateBody, "textarea");
        templateBody = encodeElement(templateBody, "pre");

        ContextRenderer form;
        boolean editing = request.getParameters().contains("edit");
        if (editing) {
            form = new SimpleContextRenderer(templateService.findBodyAsRenderer("template-edit"));
        } else {
            form = new SimpleContextRenderer(templateService.findBodyAsRenderer("template"));
        }

        form.set("templateId", id);
        form.set("templateBody", templateBody);
        form.set("template:template-list", templateListRenderer(id, editing ? "edit" : ""));

        return form;
    }

    @Instance
    @Handle(methods = {HttpMethod.POST})
    public Response idArrivePost(Request request, @Step String id) {
        Template template = templateRepository.findByName(id);

        Parameters parameters = request.getParameters();
        String templateBody = parameters.getValue("templateBody");
        templateBody = decodeElement(templateBody, "textarea");
        templateBody = decodeElement(templateBody, "pre");

        template.setBody(templateBody);
        template.setName(parameters.getValue("templateId"));
        templateRepository.save(template);

        return new RedirectResponse(request.getQuery() + "?edit");
    }

    @Instance
    @Handle(methods = {HttpMethod.DELETE})
    public Response idArriveDelete(Request request, @Step String id) {
        Template template = templateRepository.findByName(id);
        templateRepository.delete(template);
        return new RedirectResponse(request.getQuery() + "?edit");
    }

    public Renderer templateListRenderer(String id, String parameters) {
        ContextRenderer templateListRenderer = new SimpleContextRenderer(templateService.findBodyAsRenderer("template-list"));
        Renderer renderer = new GenericListRenderer<Template>()
                .preProcess(t -> {
                            OptionRenderer option = new OptionRenderer();
                            if (t.getName().equals(id)) {
                                option.attribute("selected", "selected");
                            }
                            return option.add(t.getName());
                        }
                )
                .addAll(templateRepository.findAll());
        templateListRenderer.set("templateList", renderer);
        templateListRenderer.set("parameters", parameters);
        return templateListRenderer;
    }

    public String encodeElement(String templateBody, String element) {
        templateBody = StringUtils.replace(templateBody, "<" + element, "{" + element);
        templateBody = StringUtils.replace(templateBody, "</" + element, "{/" + element);
        templateBody = StringUtils.replace(templateBody, element + ">", element + "}");
        return templateBody;
    }

    public String decodeElement(String templateBody, String element) {
        templateBody = StringUtils.replace(templateBody, "{" + element, "<" + element);
        templateBody = StringUtils.replace(templateBody, "{/" + element, "</" + element);
        templateBody = StringUtils.replace(templateBody, element + "}", element + ">");
        return templateBody;
    }
}
