package alainvanhout.cms.services;

import alainvanhout.context.Context;
import alainvanhout.cms.dtos.custom.CustomContext;
import alainvanhout.cms.dtos.stored.StoredContext;
import alainvanhout.context.provider.ContextProvider;
import alainvanhout.context.ContextException;
import alainvanhout.cms.repositories.ContextRepository;
import alainvanhout.routing.path.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContextService {

    private Map<String, CustomContext> contextMap;
    private Map<String, ContextProvider> providerMap;

    @Autowired
    private Collection<CustomContext> contexts;

    @Autowired
    private Collection<ContextProvider> providers;

    @Autowired
    private ContextRepository contextRepository;

    @PostConstruct
    private void setup() {
        contextMap = contexts.stream().collect(Collectors.toMap(CustomContext::getId, c -> c));
        providerMap = providers.stream().collect(Collectors.toMap(ContextProvider::getId, p -> p));
    }

    public Context findContext(String contextId) {
        // first try custom contexts
        if (contextMap.containsKey(contextId)) {
            return contextMap.get(contextId);
        }

        // fallback to repository
        StoredContext context = contextRepository.findOne(contextId);
        if (context == null) {
            throw new ContextException("Context does not exist: " + contextId);
        }
        return context;
    }

    public void applyContextProvider(String contextId, Path path) {
        if (providerMap.containsKey(contextId)) {
            ContextProvider provider = providerMap.get(contextId);
            provider.handle(path);
        }
    }
}