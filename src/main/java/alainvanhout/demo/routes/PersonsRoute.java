package alainvanhout.demo.routes;

import alainvanhout.demo.services.RendererService;
import alainvanhout.cms.services.ContextService;
import alainvanhout.cms.services.SectionService;
import alainvanhout.cms.services.TemplateService;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.cms.dtos.custom.CustomRoute;
import alainvanhout.routing.RoutingException;
import alainvanhout.routing.path.Path;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonsRoute implements CustomRoute {

    @Autowired
    private RendererService rendererService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ContextService contextService;

    @Override
    public String getId() {
        return "persons";
    }

    @Override
    public Renderer arrive(Path path) {
        return sectionService.arrive("persons", path);
    }

    @Override
    public Renderer proceed(Path path) {
        String personId = path.nextStep();
        path.addToContext("personId", personId);

        if (path.done()) {
            return sectionService.arrive("person", path);
        }

        String step = path.nextStep();
        if (StringUtils.equals(step, "address")) {
            return sectionService.arrive("address", path);
        }

        throw new RoutingException("Subroute unknown: " + step);
    }
}
