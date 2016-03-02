package alainvanhout.optionalrest.utils;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RequestUtils {

    public static Request toRequest(HttpServletRequest httpRequest) {
        HttpMethod method = HttpMethod.valueOf(httpRequest.getMethod());
        Request request = Request.fromQuery(httpRequest.getRequestURI(), "/root/", method);
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
