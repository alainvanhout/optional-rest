package alainvanhout.optionalrest.scope;

import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.BuildParameters;
import alainvanhout.optionalrest.scope.definition.ScopeDefinition;
import alainvanhout.optionalrest.services.mapping.Mapping;

import java.util.Map;

public interface Scope {

    Map<String, Object> buildDefinitionMap(int deep, BuildParameters params);

    ScopeDefinition getDefinition();

    Response follow(Request request);

    Scope addPassMapping(Mapping mapping);

    Scope addErrorMapping(Mapping mapping);

    void setInstanceScope(Scope scope);

    void addRelativeScope(String relative, Scope scope);

    Map<String, Scope> getRelativeScopes();

    Scope scopeId(String scopeId);

    String getScopeId();
}
