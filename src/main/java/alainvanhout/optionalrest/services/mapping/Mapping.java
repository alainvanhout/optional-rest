package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.request.RestRequest;

public interface Mapping {
    RestResponse call(RestRequest restRequest);
}
