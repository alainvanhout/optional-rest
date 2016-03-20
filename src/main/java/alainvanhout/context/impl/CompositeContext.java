package alainvanhout.context.impl;

import alainvanhout.context.Context;
import alainvanhout.context.UpdateableContext;
import alainvanhout.optionalrest.RestException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CompositeContext implements UpdateableContext {

    private UpdateableContext fallbackContext;

    private Map<String, Context> namedContexts = new HashMap<>();

    @Override
    public <T> T get(String key) {
        if (StringUtils.contains(key, ":")) {
            String contextKey = StringUtils.substringBefore(key, ":");
            if (!namedContexts.containsKey(contextKey)) {
                throw new RestException("Context not available: " + contextKey);
            }
            return namedContexts.get(contextKey).get(StringUtils.substringAfter(key, ":"));
        }

        if (fallbackContext == null) {
            return null;
        }

        return fallbackContext.get(key);
    }

    @Override
    public boolean contains(String key) {
        if (StringUtils.contains(key, ":")) {
            String contextKey = StringUtils.substringBefore(key, ":");
            if (!namedContexts.containsKey(contextKey)) {
                throw new RestException("Context not available: " + contextKey);
            }
            return namedContexts.get(contextKey).contains(StringUtils.substringAfter(key, ":"));
        }

        if (fallbackContext != null) {
            return fallbackContext.contains(key);
        }

        return false;
    }

    public CompositeContext fallbackContext(UpdateableContext fallbackContext) {
        this.fallbackContext = fallbackContext;
        return this;
    }

    public CompositeContext addNamedContexts(String key, Context value) {
        namedContexts.put(key, value);
        return this;
    }

    @Override
    public UpdateableContext add(String key, Object value) {
        if (fallbackContext != null) {
            fallbackContext.add(key, value);
        }
        return this;
    }
}
