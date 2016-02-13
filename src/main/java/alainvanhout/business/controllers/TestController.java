package alainvanhout.business.controllers;

import alainvanhout.business.Address;
import alainvanhout.business.renderers.PersonRenderer;
import alainvanhout.business.repositories.PersonRepository;
import alainvanhout.business.restservices.PersonRestService;
import alainvanhout.business.restservices.RootScope;
import alainvanhout.business.services.RendererService;
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
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.services.ScopeManager;
import alainvanhout.routing.path.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class TestController {

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
    private PersonRestService personRestService;

    @Autowired
    private RootScope rootScope;

    @Autowired
    private ScopeManager scopeManager;

    @RequestMapping(value = "/root/**")
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

    @RequestMapping(value = "/test/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    public String test(HttpServletRequest httpRequest) {
        HttpMethod method = HttpMethod.valueOf(httpRequest.getMethod());
        RestRequest restRequest = RestRequest.fromQuery(httpRequest.getRequestURI(), "/test/", method);

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
