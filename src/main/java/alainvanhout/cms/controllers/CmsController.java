package alainvanhout.cms.controllers;

import alainvanhout.demo.Template;
import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.routing.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
@ResponseBody
@RequestMapping(value = "/content/**", produces = MediaType.TEXT_HTML_VALUE)
public class CmsController {

    @Autowired
    private TemplateRepository templateRepository;

    private Route templatesRoute;

    //@PostConstruct
    private void setup() {

        //templatesRoute = new SimpleRoute(new RouteRenderer(new StringRenderer(template.getBody())));
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request) {
//        Route route = new PathsRoute()
//                .addRoute("foo", new StringRenderer("bar"))
//                .addRoute("foo2", new StringRenderer("bar2"))
//                .addRoute("foo3", new StringRenderer("bar3"));
//
//        return route.follow(new Direction("foo34")).render();

        Template main = templateRepository.findByName("main");

        //templatesRoute = new SimpleRoute(new RouteRenderer(new StringRenderer(main.getBody())));

        //return templatesRoute.follow().render();
        return null;
    }
}
