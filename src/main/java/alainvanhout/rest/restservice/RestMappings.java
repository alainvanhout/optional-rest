package alainvanhout.rest.restservice;

import alainvanhout.rest.request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class RestMappings {

    private Map<HttpMethod, RestMapping> mappings = new HashMap<>();

    public void addMapping(HttpMethod method, RestMapping mapping){
        mappings.put(method, mapping);
    }

    public boolean contains(HttpMethod method){
        return mappings.containsKey(method);
    }

    public RestMapping get(HttpMethod method){
        return mappings.get(method);
    }
}
