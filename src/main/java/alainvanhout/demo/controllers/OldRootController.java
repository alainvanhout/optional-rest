package alainvanhout.demo.controllers;

import renderering.core.basic.StringRenderer;
import renderering.web.webpage.WebpageRenderer;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

//@Controller
//@ResponseBody
//@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class OldRootController {

    @RequestMapping(value = "/test/**")
    public String test(HttpServletRequest request) {
        WebpageRenderer page = new WebpageRenderer()
                .body(new StringRenderer("foo"))
                .stylesheet("css/style.css")
                .script("js/script.js");
        return page.render();
    }
}
