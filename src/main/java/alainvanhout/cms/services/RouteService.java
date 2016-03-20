package alainvanhout.cms.services;

import alainvanhout.context.impl.SimpleRendererContext;
import alainvanhout.routing.RoutingException;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.cms.repositories.SwitchRouteRepository;
import alainvanhout.cms.dtos.custom.CustomRoute;
import alainvanhout.routing.Route;
import alainvanhout.cms.dtos.stored.StoredRoute;
import alainvanhout.routing.path.Path;
import alainvanhout.sections.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private Map<String, CustomRoute> routeMap;

    @Autowired
    private Collection<CustomRoute> routes;

    @Autowired
    private SwitchRouteRepository switchRouteRepository;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CmsContextService contextService;

    @PostConstruct
    private void setup() {
        routeMap = routes.stream().collect(Collectors.toMap(CustomRoute::getId, r -> r));
    }

    public Route findRoute(String routeId) {
        // first try custom sections
        if (routeMap.containsKey(routeId)) {
            return routeMap.get(routeId);
        }

        // fallback to repository
        StoredRoute route = switchRouteRepository.findOne(routeId);
        if (route == null) {
            throw new RoutingException("Route does not exist: " + routeId);
        }
        return route;
    }

    public Renderer follow(Route route, Path path) {
        if (route instanceof CustomRoute) {
            return ((CustomRoute) route).follow(path);
        } else if (route instanceof StoredRoute) {
            return follow((StoredRoute) route, path);
        }
        throw new RoutingException("Unable to follow route");
    }

    public Renderer follow(StoredRoute route, Path path) {

        // TODO: 404/500 error routes/sectins

        for (String contextId : route.getContexts()) {
            path.addContext(contextService.findContext(contextId));
            contextService.applyContextProvider(contextId, path);
        }

        if (path.done()) {
            // arrived at end of path -> use section or template
            return arrive(route, path);
        } else {
            // not yet arrived at end of path
            // -> check next step: leads to another route or to a section? (TODO: or a template?)
            String step = path.nextStep();

            // try routes
            if (route.hasSubroute(step)) {
                String routeId = route.getRouteForStep(step);
                Route subroute = findRoute(routeId);
                return follow(subroute, path);
            }

            // try sections
            if (route.hasSection(step)) {
                String sectionId = route.getSectionForStep(step);
                Section section = sectionService.findSection(sectionId);
                sectionService.arrive(section, path);
            }

            // try templates
            if (route.hasTemplate(step)) {
                String templateId = route.getTemplateForStep(step);
                return templateService.findBodyAsRenderer(templateId);
            }

            throw new RoutingException("Unable to follow route step: " + step);
        }
    }

    private Renderer arrive(StoredRoute route, Path path) {
        if (route.getSectionForStep() != null) {
            return sectionService.arrive(route.getSectionForStep(), path);
        } else if (route.getTemplateId() != null) {
            Renderer body = templateService.findBodyAsRenderer(route.getTemplateId());
            return new SimpleContextRenderer(body, new SimpleRendererContext(path.getContext()));
        } else {
            throw new RoutingException("No section or template specified for route " + route.getId());
        }
    }
}
