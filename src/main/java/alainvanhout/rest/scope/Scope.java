package alainvanhout.rest.scope;

import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.meta.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.services.mapping.Mapping;

public interface Scope {

    ScopeDefinition getDefinition();

    RestResponse follow(RestRequest restRequest);

    Scope addPassMapping(Mapping mapping, HttpMethod... methods);

    Scope addArriveMapping(Mapping mapping, HttpMethod... methods);

    Scope addErrorMapping(Mapping mapping, HttpMethod... methods);

    void setInstanceScope(Scope scope);

    void addRelativeScope(String relative, Scope scope);

}
