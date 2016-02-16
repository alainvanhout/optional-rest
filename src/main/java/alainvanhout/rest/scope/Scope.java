package alainvanhout.rest.scope;

import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.meta.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.services.RestMapping;

public interface Scope {

    RestResponse follow(RestRequest restRequest);

    ScopeDefinition getDefinition();

    Scope addPassMapping(RestMapping mapping, HttpMethod... methods);

    Scope addArriveMapping(RestMapping mapping, HttpMethod... methods);

    Scope addFallbackMapping(RestMapping mapping, HttpMethod... methods);

    Scope addErrorMapping(RestMapping mapping, HttpMethod... methods);

    void addRelativeMapping(String relative, RestMapping mapping);
}
