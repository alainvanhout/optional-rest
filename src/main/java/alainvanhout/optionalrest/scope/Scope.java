package alainvanhout.optionalrest.scope;

import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.services.mapping.Mapping;

import java.util.Map;

public interface Scope {

    Map<String, Object> buildDefinitionMap(int deep, BuildParameters params);

    ScopeDefinition getDefinition();

    Response follow(RestRequest restRequest);

    Scope addPassMapping(Mapping mapping, HttpMethod... methods);

    Scope addArriveMapping(Mapping mapping, HttpMethod... methods);

    Scope addErrorMapping(Mapping mapping, HttpMethod... methods);

    void setInstanceScope(Scope scope);

    void addRelativeScope(String relative, Scope scope);

    GenericScope scopeId(String scopeId);

    String getScopeId();
}
