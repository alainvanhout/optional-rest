package alainvanhout.rest.services;

import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.services.factories.ResourceScopeFactory;
import alainvanhout.rest.services.factories.ScopeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class ScopeManager {

    @Autowired
    private ScopeRegistry scopeRegistry;

    @Autowired
    private ResourceScopeFactory scopeFactory;

    @Autowired
    private Collection<ScopeContainer> containers;

    @Autowired
    private Collection<ScopeFactory> factories;

    @PostConstruct
    public void setup() {
        for (ScopeFactory factory : factories) {
            for (ScopeContainer scopeContainer : containers) {
                factory.processContainer(scopeContainer);
            }
        }
    }

    public RestResponse follow(ScopeContainer container, RestRequest restRequest) {
        return scopeRegistry.findByContainer(container).follow(restRequest);
    }
}
