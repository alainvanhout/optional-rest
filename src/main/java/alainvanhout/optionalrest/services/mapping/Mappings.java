package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.Supported;
import org.apache.commons.lang3.StringUtils;

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
                && typeSupported(m.getSupported().getAccept(), request.getHeaders().get("accept"))
                && typeSupported(m.getSupported().getContentType(), request.getHeaders().get("content-type"));
    }

    private boolean typeSupported(Collection<String> supportedTypes, Collection<String> neededTypes){
        if (neededTypes == null){
           return true;
        }
        for (String neededType : neededTypes) {
            if (supportedTypes.stream().anyMatch(s -> {
                String[] supportedSplit = StringUtils.split(s, "/");
                String[] neededSplit = StringUtils.split(neededType, "/");
                if (supportedSplit.length != 2){
                    throw new RestException("Mime type of incorrect form:" + s );
                }
                if (neededSplit.length != 2){
                    throw new RestException("Mime type of incorrect form:" + neededType );
                }
                if (StringUtils.equals(supportedSplit[0], "*")){
                    return true;
                }
                if (StringUtils.equals(supportedSplit[0], neededSplit[0])){
                    if (StringUtils.equals(supportedSplit[1], "*")){
                        return true;
                    }
                    if (StringUtils.equals(supportedSplit[0], neededSplit[0])){
                        return true;
                    }
                }
                return false;
            })){
                return true;
            }
        }
        return false;
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

    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(", "));
    }
}
