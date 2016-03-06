package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.Supported;

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
                m -> supports(request, m)
        ).collect(Collectors.toList());
        if (matches.size() == 0) {
            return null;
        }
        if (matches.size() > 1) {
            throw new RestException("Multiple matching mappings found");
        }
        return matches.get(0);
    }

    public boolean supports(Request request, Mapping m) {
        return m.getSupported().getMethods().contains(request.getMethod())
                && overlap(m.getSupported().getAccept(), request.getHeaders().get("accept"))
                && overlap(m.getSupported().getContentType(), request.getHeaders().get("content-type"));
    }

    public boolean overlap(Collection<String> collection1, Collection<String> collection2) {
        return collection1 == null || collection2 == null
                || collection1.isEmpty() || collection2.isEmpty()
                || !Collections.disjoint(collection1, collection2);
    }

    public Supported supported() {
        Supported supported = new Supported();
        for (Mapping mapping : this.list) {
            supported.getMethods().addAll(mapping.getSupported().getMethods());
            supported.getAccept().addAll(mapping.getSupported().getAccept());
            supported.getContentType().addAll(mapping.getSupported().getContentType());
        }
        return supported;
    }
}
