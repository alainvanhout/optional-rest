package alainvanhout.rest.scope;

import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.services.RestMapping;

public interface BasicScope extends Scope {
    BasicScope addPassMapping(RestMapping mapping, HttpMethod... methods);

    BasicScope addArriveMapping(RestMapping mapping, HttpMethod... methods);

    BasicScope addFallbackMapping(RestMapping mapping, HttpMethod... methods);

    BasicScope addErrorMapping(RestMapping mapping, HttpMethod... methods);

    SimpleScope addRelativeMapping(String relative, RestMapping mapping);
}
