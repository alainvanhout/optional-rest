package optionalrest.spring.utils;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.core.request.meta.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RequestUtils {

    public static Request toRequest(HttpServletRequest httpRequest) {
        Request request = Request.fromQuery(httpRequest.getRequestURI());
        request.method(HttpMethod.valueOf(httpRequest.getMethod()));
        request.getParameters().addAll(httpRequest.getParameterMap());

        List<String> headerNames = Collections.list(httpRequest.getHeaderNames());
        for (String headerName : headerNames) {
            request.getHeaders().add(headerName, httpRequest.getHeader(headerName));
        }

        try {
            request.reader(httpRequest.getReader());
        } catch (IOException e) {
            throw new RestException("Encountered error while retrieving request reader", e);
        }

        return request;
    }

}
