package alainvanhout.optionalrest.utils;

import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.request.meta.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

public class RequestUtils {

    public static RestRequest toRequest(HttpServletRequest httpRequest) {
        HttpMethod method = HttpMethod.valueOf(httpRequest.getMethod());
        RestRequest restRequest = RestRequest.fromQuery(httpRequest.getRequestURI(), "/root/", method);
        restRequest.getParameters().addAll(httpRequest.getParameterMap());

        List<String> headerNames = Collections.list(httpRequest.getHeaderNames());
        for (String headerName : headerNames) {
            restRequest.getHeaders().add(headerName, httpRequest.getHeader(headerName));
        }
        return restRequest;
    }

}
