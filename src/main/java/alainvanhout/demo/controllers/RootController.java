package alainvanhout.demo.controllers;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.renderers.PersonRenderer;
import alainvanhout.demo.repositories.PersonRepository;
import alainvanhout.demo.scopes.PersonScope;
import alainvanhout.demo.scopes.RootScope;
import alainvanhout.demo.services.RendererService;
import alainvanhout.cms.dtos.stored.StoredRoute;
import alainvanhout.cms.repositories.SwitchRouteRepository;
import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.cms.services.ContextService;
import alainvanhout.cms.services.RouteService;
import alainvanhout.cms.services.SectionService;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;
import alainvanhout.renderering.renderer.retrieve.FetchingRenderer;
import alainvanhout.renderering.renderer.webpage.WebpageRenderer;
import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.services.ScopeManager;
import alainvanhout.routing.path.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class RootController {

    @Autowired
    private RendererService rendererService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SwitchRouteRepository switchRouteRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private ContextService contextService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private PersonScope personScope;

    @Autowired
    private RootScope rootScope;

    @Autowired
    private ScopeManager scopeManager;

    @RequestMapping(value = "/test/**")
    public String root(HttpServletRequest request) {

        Path path = Path.fromQuery(request.getRequestURI(), "/root/");
        StoredRoute root = switchRouteRepository.findOne("root");
        Renderer renderer = routeService.follow(root, path);
        WebpageRenderer page = new WebpageRenderer()
                .body(renderer)
                .stylesheet("css/style.css")
                .script("js/script.js");
        return page.render();
    }

    @RequestMapping(value = "/root/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    public String test(HttpServletRequest httpRequest) {
        HttpMethod method = HttpMethod.valueOf(httpRequest.getMethod());
        RestRequest restRequest = RestRequest.fromQuery(httpRequest.getRequestURI(), "/root/", method);
        restRequest.getParameters().addAll(httpRequest.getParameterMap());

        List<String> headerNames = Collections.list(httpRequest.getHeaderNames());
        for (String headerName : headerNames) {
            restRequest.getHeaders().add(headerName, httpRequest.getHeader(headerName));
        }
        RestResponse response = scopeManager.follow(rootScope, restRequest);
        return response.render();
    }

    public FetchingRenderer<String> template(String s) {
        return new FetchingRenderer<String>(rendererService, s);
    }

    private SimpleModelRenderer<Address> getAddressRenderer() {
        return new SimpleModelRenderer<>(template("address"));
    }

    private PersonRenderer getLargePersonRenderer() {
        return new PersonRenderer(template("person-large"));
    }

}
