package optionalrest.core.scope;

import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.ScopeDefinition;
import optionalrest.core.services.mapping.Mapping;

import java.util.Map;

public interface Scope {

    void pass(Request request);

    Response arrive(Request request);

    ScopeDefinition getDefinition();

    Response follow(Request request);

    Scope addPassMapping(Mapping mapping);

    Scope addErrorMapping(Mapping mapping);

    void setInstanceScope(Scope scope);

    void addRelativeScope(String relative, Scope scope);

    Map<String, Scope> getRelativeScopes();

    Scope scopeId(String scopeId);

    String getScopeId();

    GenericScope optionsRequestHandler(OptionsRequestHandler optionsRequestHandler);

    Supported getSupported();

    Scope getInstanceScope();
}
