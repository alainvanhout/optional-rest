package alainvanhout.demo.scopes;

import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.cms.services.TemplateService;
import alainvanhout.demo.Template;
import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.demo.renderers.PersonRenderer;
import alainvanhout.demo.repositories.PersonRepository;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.annotations.instance.RestInstance;
import alainvanhout.optionalrest.annotations.instance.RestInstanceRelative;
import alainvanhout.optionalrest.annotations.resource.RestRelative;
import alainvanhout.optionalrest.request.Headers;
import alainvanhout.optionalrest.request.Parameters;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.services.factories.FromContext;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.ContextRenderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.DivRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.LinkRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.ParagraphRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.select.OptionRenderer;
import alainvanhout.renderering.renderer.list.GenericListRenderer;
import alainvanhout.renderering.renderer.list.ListRenderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;
import alainvanhout.renderering.renderer.retrieve.TextResourceRenderer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static alainvanhout.optionalrest.request.meta.Header.Accept.Text.HTML;

@Service
@ScopeDefinition(name = "template")
@EntityDefinition(instanceClass = Template.class)
public class TemplateScope implements ScopeContainer {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateRepository templateRepository;

    @RestInstance(methods = {HttpMethod.GET, HttpMethod.POST})
    public Renderer idArrive(Request request) {
        String id = request.getPath().getStep();
        Template template = templateRepository.findByName(id);

        if (HttpMethod.POST.equals(request.getMethod())){
            Parameters parameters = request.getParameters();
            template.setBody(parameters.getValue("templateBody"));
            template.setName(parameters.getValue("templateId"));
            templateRepository.save(template);
        }
        String templateBody = template.getBody();

        ContextRenderer form = new SimpleContextRenderer(new TextResourceRenderer("templates/edit-templates.html"));
        ListRenderer renderer = new GenericListRenderer<Template>()
                .preProcess(t -> new OptionRenderer().add(new LinkRenderer().href(t.getName()).add(t.getName())))
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
