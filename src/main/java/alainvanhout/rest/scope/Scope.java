package alainvanhout.rest.scope;

import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.RestRequest;

public interface Scope {

    RestResponse follow(RestRequest restRequest);
}
