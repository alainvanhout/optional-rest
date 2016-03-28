package alainvanhout.demo.scopes;

import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.cms.services.TemplateService;
import alainvanhout.demo.Template;
import optionalrest.core.annotations.EntityDefinition;
import optionalrest.core.annotations.ScopeDefinition;
import optionalrest.core.annotations.requests.methods.Delete;
import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.annotations.requests.methods.Post;
import optionalrest.core.annotations.scopes.Instance;
import optionalrest.core.request.Parameters;
import optionalrest.core.request.Request;
import optionalrest.core.response.RedirectResponse;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.factories.Step;
import optionalrest.core.utils.JsonUtils;
import renderering.core.Renderer;
import renderering.core.context.ContextRenderer;
import renderering.core.context.SimpleContextRenderer;
import renderering.web.html.basic.documentbody.PreRenderer;
import renderering.web.html.basic.documentbody.select.OptionRenderer;
import renderering.core.list.GenericListRenderer;
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

    @Get
    public Renderer arrive(Request request) {
        return new PreRenderer(JsonUtils.objectToJson(templateRepository.findAll()));
    }

    @Instance @Get
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

    @Instance @Post
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

    @Instance @Delete
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
