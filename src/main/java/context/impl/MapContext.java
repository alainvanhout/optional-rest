package context.impl;

import context.UpdateableContext;

import java.util.HashMap;
import java.util.Map;

public class MapContext implements UpdateableContext {
    private Map<String, Object> map;

    public MapContext() {
        this.map = new HashMap<>();
    }

    public MapContext(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public UpdateableContext add(String key, Object value) {
        map.put(key, value);
        return this;
    }
}
