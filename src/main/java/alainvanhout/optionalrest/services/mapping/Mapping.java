package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.response.Response;

public interface Mapping {
    Response call(RestRequest restRequest);
}
