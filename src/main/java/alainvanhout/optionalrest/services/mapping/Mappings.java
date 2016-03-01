package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.request.meta.HttpMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Mappings {

    private List<Mapping> list = new ArrayList<>();

    public Mappings add(Mapping mapping) {
        list.add(mapping);
        return this;
    }

    public Mapping getMapping(RestRequest restRequest) {
        List<Mapping> matches = list.stream().filter(
                m -> m.supports(HttpMethod.class.getName(), restRequest.getMethod())
        ).collect(Collectors.toList());
        if (matches.size() == 0) {
            return null;
        }
        if (matches.size() > 1) {
            throw new RestException("Multiple matching mappings found");
        }
        return matches.get(0);
    }

    public <T> Set<T> supported(String key) {
        Set supported = new HashSet<>();
        for (Mapping mapping : list) {
            supported.addAll(mapping.supported(key));
        }
        return supported;
    }
}
