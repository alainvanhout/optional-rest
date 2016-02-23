package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.request.meta.HttpMethod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public Set<HttpMethod> supportedMethods(){
        return new HashSet<>(mappings.keySet());
    }
}
