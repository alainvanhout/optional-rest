package alainvanhout.demo.controllers;

import alainvanhout.cms.dtos.stored.StoredRoute;
import alainvanhout.cms.repositories.SwitchRouteRepository;
import alainvanhout.cms.services.RouteService;
import alainvanhout.demo.scopes.RootScope;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.services.ScopeManager;
import alainvanhout.optionalrest.utils.RequestUtils;
import alainvanhout.optionalrest.utils.ResponseUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.webpage.WebpageRenderer;
import alainvanhout.routing.path.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@ResponseBody
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class RootController {

    @Autowired
    private SwitchRouteRepository switchRouteRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private RootScope rootScope;

    @Autowired
    private ScopeManager scopeManager;

    @RequestMapping(value = "/test/**")
    public String test(HttpServletRequest request) {

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
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
            produces = {"*/*"}
    )
    public ResponseEntity root(HttpServletRequest httpRequest) {
        Request request = RequestUtils.toRequest(httpRequest);
        Response response = scopeManager.follow(rootScope, request);
        return ResponseUtils.toResponseEntity(response);
    }
}
