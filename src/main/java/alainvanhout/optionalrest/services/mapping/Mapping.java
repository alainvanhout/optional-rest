package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.response.Response;

import java.util.Collection;
import java.util.Set;

public interface Mapping {
    Response call(RestRequest restRequest);

    Set<Object> supported(String key);

    boolean supports(String key, Object value);

    Mapping supportAll(String key, Collection<Object> value);
}
