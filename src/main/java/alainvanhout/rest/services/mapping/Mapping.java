package alainvanhout.rest.services.mapping;

import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.RestRequest;

public interface Mapping {
    RestResponse call(RestRequest restRequest);
}
