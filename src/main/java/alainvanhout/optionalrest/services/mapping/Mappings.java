package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.Request;

import java.util.*;
import java.util.stream.Collectors;

public class Mappings {

    private List<Mapping> list = new ArrayList<>();

    public Mappings add(Mapping mapping) {
        list.add(mapping);
        return this;
    }

    public Mapping getMapping(Request request) {
        List<Mapping> matches = list.stream().filter(
                m -> m.getSupported().getMethods().contains(request.getMethod())
                && overlap(m.getSupported().getAccepts(), request.getHeaders().get("accept"))
        ).collect(Collectors.toList());
        if (matches.size() == 0) {
            return null;
        }
        if (matches.size() > 1) {
            throw new RestException("Multiple matching mappings found");
        }
        return matches.get(0);
    }

    public boolean overlap(Collection<String> accepts, Collection<String> accept) {
        return !Collections.disjoint(accepts, accept);
    }

    public <T> Set<T> supportedMethods() {
        Set supported = new HashSet<>();
        for (Mapping mapping : list) {
            supported.addAll(mapping.getSupported().getMethods());
        }
        return supported;
    }
}
