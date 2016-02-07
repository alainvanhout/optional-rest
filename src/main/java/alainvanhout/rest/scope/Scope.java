package alainvanhout.rest.scope;

import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.restservice.RestMapping;

public interface Scope {

    RestResponse follow(RestRequest restRequest);

    BasicScope addPassMapping(RestMapping mapping, HttpMethod... methods);

    BasicScope addArriveMapping(RestMapping mapping, HttpMethod... methods);

    BasicScope addFallbackMapping(RestMapping mapping, HttpMethod... methods);

    BasicScope addErrorMapping(RestMapping mapping, HttpMethod... methods);
}
