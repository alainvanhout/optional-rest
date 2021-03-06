package optionalrest.core.services.mapping;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.request.meta.Mime;
import optionalrest.core.scope.Supported;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Mappings {

    private List<RequestHandler> list = new ArrayList<>();

    public Mappings add(RequestHandler requestHandler) {
        list.add(requestHandler);
        return this;
    }

    public List<RequestHandler> getMappings() {
        return list.stream()
                .sorted((m1, m2) -> m1.getOrder() - m2.getOrder())
                .collect(Collectors.toList());
    }

    public List<RequestHandler> getMappings(Request request, boolean passing) {
        return list.stream()
                .filter(m -> supports(request, m.getSupported()) && m.isPassing() == passing)
                .sorted((m1, m2) -> m1.getOrder() - m2.getOrder())
                .collect(Collectors.toList());
    }

    private boolean supports(Request request, Supported supported) {
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
        if (neededTypes == null ) {
            return true;
        }
        for (String neededType : neededTypes) {
            if (supportedTypes.stream().anyMatch(s -> {
                if (neededType.equals("*")){
                    return true;
                }
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

    public Supported getSupported() {
        Supported supported = new Supported();
        for (RequestHandler requestHandler : this.list) {
            supported.getMethods().addAll(requestHandler.getSupported().getMethods());
            supported.getAccept().addAll(requestHandler.getSupported().getAccept());
            supported.getContentType().addAll(requestHandler.getSupported().getContentType());
        }
        return supported;
    }

    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(", "));
    }
}
