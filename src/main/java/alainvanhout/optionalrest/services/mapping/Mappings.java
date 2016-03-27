package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.request.meta.Mime;
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

    public List<Mapping> getMappings(Request request) {
        return list.stream()
                .filter(m -> supports(request, m.getSupported()))
                .sorted((m1, m2) -> m1.getOrder() - m2.getOrder())
                .collect(Collectors.toList());
    }

    public List<Mapping> getMappings(Request request, boolean passing) {
        return list.stream()
                .filter(m -> supports(request, m.getSupported()) && m.isPassing() == passing)
                .sorted((m1, m2) -> m1.getOrder() - m2.getOrder())
                .collect(Collectors.toList());
    }

    public boolean supports(Request request, Supported supported) {
        // methods or defaults
        Collection<HttpMethod> methods = supported.getMethods();
        if (methods.isEmpty()){
            methods = Arrays.asList(HttpMethod.values());
        }
        // accept or defaults
        Collection<String> accept = supported.getAccept();
        if (accept.isEmpty()){
            accept = Collections.singletonList(Mime.ALL);
        }
        // accept or defaults
        Collection<String> contentType = supported.getContentType();
        if (contentType.isEmpty()){
            contentType = Collections.singletonList(Mime.ALL);
        }

        return methods.contains(request.getMethod())
                && typeSupported(accept, request.getHeaders().get("accept"))
                && typeSupported(contentType, request.getHeaders().get("content-type"));
    }

    private boolean typeSupported(Collection<String> supportedTypes, Collection<String> neededTypes) {
        if (neededTypes == null) {
            return true;
        }
        for (String neededType : neededTypes) {
            if (supportedTypes.stream().anyMatch(s -> {
                String[] supportedSplit = StringUtils.split(s, "/");
                String[] neededSplit = StringUtils.split(neededType, "/");
                if (supportedSplit.length != 2) {
                    throw new RestException("Mime type of incorrect form:" + s);
                }
                if (neededSplit.length != 2) {
                    throw new RestException("Mime type of incorrect form:" + neededType);
                }
                if (StringUtils.equals(supportedSplit[0], "*")) {
                    return true;
                }
                if (StringUtils.equals(supportedSplit[0], neededSplit[0])) {
                    if (StringUtils.equals(supportedSplit[1], "*")) {
                        return true;
                    }
                    if (StringUtils.equals(supportedSplit[1], neededSplit[1])) {
                        return true;
                    }
                }
                return false;
            })) {
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
