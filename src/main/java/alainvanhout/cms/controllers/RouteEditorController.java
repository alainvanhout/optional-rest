package alainvanhout.cms.controllers;

import alainvanhout.cms.repositories.SwitchRouteRepository;
import alainvanhout.cms.services.RouteService;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.ContextRenderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.SpanRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.list.ListItemRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.list.UnorderedListRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.select.OptionRenderer;
import alainvanhout.renderering.renderer.list.GenericListRenderer;
import alainvanhout.renderering.renderer.list.ListRenderer;
import alainvanhout.renderering.renderer.manage.SafeRenderer;
import alainvanhout.renderering.renderer.retrieve.TextResourceRenderer;
import alainvanhout.cms.dtos.stored.StoredRoute;
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
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@ResponseBody
@RequestMapping(value = "routes/", produces = MediaType.TEXT_HTML_VALUE)
public class RouteEditorController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private SwitchRouteRepository routeRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String getForm(@RequestParam(value = "routeId", required = false) String routeId) {
        return composeForm(routeId).render();
    }

    public Renderer composeForm(String routeId) {
        ContextRenderer form = new SimpleContextRenderer(new TextResourceRenderer("templates/edit-routes.html"));

        String subrouteText = "";
        String contextsText = "";
        String templateId = "";
        if (routeId != null) {
            StoredRoute route = routeRepository.findOne(routeId);
            subrouteText = route.getRoutes().entrySet().stream()
                    .map(r -> r.getKey() + ":" + r.getValue()).collect(Collectors.joining("\r\n"));
            contextsText = route.getContexts().stream().collect(Collectors.joining("\r\n"));
            templateId = route.getTemplateId();
        }

        ListRenderer routeList = new GenericListRenderer<StoredRoute>()
                .preProcess(t -> new OptionRenderer().add(t.getId()))
                .addAll(routeRepository.findAll());

        if (routeId != null && StringUtils.isNotBlank(subrouteText)) {
            form.set("routeId", routeId);
            form.set("templateId", templateId);
            form.set("subroutes", subrouteText);
            form.set("contexts", contextsText);
        } else {
            form.set("routeId", "");
            form.set("templateId", "");
            form.set("subroutes", "");
            form.set("contexts", "");
        }
        form.set("routeList", routeList);
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

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String getList(@RequestParam(value = "templateId", required = false) String templateId) {
        UnorderedListRenderer ul = new UnorderedListRenderer();
        ul.add(listItemFor("root", "root"));
        return ul.render();
    }

    private ListItemRenderer listItemFor(String step, String routeId){
        ListItemRenderer li = new ListItemRenderer();
        li.add(new SpanRenderer(step));

        StoredRoute route = routeRepository.findOne(routeId);
        if (route != null) {
            UnorderedListRenderer ul = new UnorderedListRenderer();
            for (Map.Entry<String, String> subroute : route.getRoutes().entrySet()) {
                ul.add(listItemFor(subroute.getKey(), subroute.getValue()));
            }
            li.add(ul);
        }
        return li;
    }

    @RequestMapping(value = "store", method = RequestMethod.POST)
    public void getFormPost(@RequestParam("routeId") String routeId,
                            @RequestParam("templateId") String templateId,
                            @RequestParam("subroutes") String subrouteText,
                            @RequestParam("contexts") String contextText,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        StoredRoute route = new StoredRoute()
                .id(routeId)
                .templateId(templateId);

        String[] subroutes = StringUtils.split(subrouteText, "\r\n");
        for (String subroute : subroutes) {
            String key = StringUtils.substringBefore(subroute, ":");
            String value = StringUtils.contains(subroute, ":") ? StringUtils.substringAfter(subroute, ":") : "";
            route.addRoute(key, value);
        }

        String[] contexts = StringUtils.split(contextText, "\r\n");
        route.setContexts(Arrays.asList(contexts));

        routeRepository.save(route);
        response.sendRedirect("?routeId=" + routeId);
    }
}
