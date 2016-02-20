package alainvanhout.rest.services.mapping;

import alainvanhout.rest.request.meta.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class RestMappings {

    private Map<HttpMethod, Mapping> mappings = new HashMap<>();

    public void addMapping(HttpMethod method, Mapping mapping){
        mappings.put(method, mapping);
    }

    public boolean contains(HttpMethod method){
        return mappings.containsKey(method);
    }

    public Mapping get(HttpMethod method){
        return mappings.get(method);
    }
}
