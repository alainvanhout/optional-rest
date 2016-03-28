package alainvanhout.demo.controllers;

import alainvanhout.cms.dtos.stored.StoredRoute;
import alainvanhout.cms.repositories.SwitchRouteRepository;
import alainvanhout.cms.services.RouteService;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.webpage.WebpageRenderer;
import alainvanhout.routing.path.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

//@Controller
//@ResponseBody
//@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class OldRootController {

    @Autowired
    private SwitchRouteRepository switchRouteRepository;

    @Autowired
    private RouteService routeService;

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
}
