package optionalrest.cms.services;

import optionalrest.cms.cms.dtos.custom.CustomContext;
import context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ContextRegistry {

    @Autowired
    private Collection<CustomContext> customContexts;

    private Map<String, Context> contexts = new HashMap<>();

    @PostConstruct
    private void setup() {
        for (CustomContext customContext : customContexts) {
            contexts.put(customContext.getId(), customContext);
        }
    }

    public Context get(String name) {
        if (!contexts.containsKey(name)) {
            return null;
        }
        return contexts.get(name);
    }

    public boolean contains(String name) {
        return contexts.containsKey(name);
    }
}
