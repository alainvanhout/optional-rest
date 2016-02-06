package alainvanhout.cms.controllers;

import alainvanhout.business.Template;
import alainvanhout.business.services.RendererService;
import alainvanhout.context.RendererContext;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.context.ContextRenderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.DivRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.select.OptionRenderer;
import alainvanhout.renderering.renderer.html.quick.DOM;
import alainvanhout.renderering.renderer.list.GenericListRenderer;
import alainvanhout.renderering.renderer.list.ListRenderer;
import alainvanhout.renderering.renderer.list.SimpleListRenderer;
import alainvanhout.renderering.renderer.manage.SafeRenderer;
import alainvanhout.renderering.renderer.retrieve.FetchingRenderer;
import alainvanhout.renderering.renderer.retrieve.TextResourceRenderer;
import alainvanhout.cms.repositories.TemplateRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ResponseBody
@RequestMapping(value = "templates/", produces = MediaType.TEXT_HTML_VALUE)
public class TemplateEditorController {

    @Autowired
    private RendererService rendererService;

    @Autowired
    private TemplateRepository templateRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String getForm(@RequestParam(value = "templateId", required = false) String templateId) {
        SimpleContextRenderer contextRenderer = new SimpleContextRenderer(new FetchingRenderer<>(rendererService, templateId), new RendererContext() {
            @Override
            public Renderer get(String key) {
                if (StringUtils.startsWith(key, "template:")) {
                    key = StringUtils.substringAfter(key, "template:");
                    return DOM.span().a(key).href("?templateId=" + key);
                } else {
                    return new StringRenderer(key);
                }
            }

            @Override
            public boolean contains(String key) {
                return true;
            }
        });
        return new SimpleListRenderer()
                .add(new DivRenderer(printErrors(composeForm(templateId))))
                .add(new DivRenderer(printErrors(contextRenderer)))
                .render();
    }

    public Renderer composeForm(String templateId) {
        ContextRenderer form = new SimpleContextRenderer(new TextResourceRenderer("templates/edit-templates.html"));
        String templateBody = templateRepository.findByName(templateId).getBody();

        ListRenderer renderer = new GenericListRenderer<Template>()
                .preProcess(t -> new OptionRenderer().add(t.getName()))
                .addAll(templateRepository.findAll());

        if (templateId != null && StringUtils.isNotBlank(templateBody)) {
            form.set("templateId", templateId);
            form.set("templateBody", templateBody);
            form.set("templateList", renderer);
        } else {
            form.set("templateId", "");
            form.set("templateBody", "");
            form.set("templateList", renderer);
        }
        return form;
    }

    public Renderer printErrors(Renderer renderer) {
        return new SafeRenderer(renderer, new SafeRenderer.Handler() {
            @Override
            public String thenReturn(Renderer renderer, Exception e) {
                return "<em>Rendering failed.</em> " + e.getMessage();
            }
        });
    }

    @RequestMapping(value = "store", method = RequestMethod.POST)
    public void getFormPost(@RequestParam("templateId") String templateId,
                            @RequestParam("templateBody") String templateBody,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        templateRepository.save(new Template(templateId, templateBody));
        response.sendRedirect("?templateId=" + templateId);
    }
}
