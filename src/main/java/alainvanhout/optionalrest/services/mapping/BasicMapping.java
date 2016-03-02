package alainvanhout.optionalrest.services.mapping;

import java.util.*;

public abstract class BasicMapping implements Mapping {

    private Map<String, Set<Object>> map = new HashMap<>();

    @Override
    public boolean supports(String key, Object value) {
        return map.containsKey(key) && map.get(key).contains(value);
    }

    @Override
    public Set<Object> supported(String key) {
        if (map.containsKey(key)){
            return new HashSet<>(map.get(key));
        }
        return new HashSet<>();
    }

    @Override
    public Mapping supportAll(String key, Collection<Object> value) {
        if (!map.containsKey(key)) {
            map.put(key, new HashSet<>());
        }
        map.get(key).addAll(value);
        return this;
    }
}
