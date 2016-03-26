package alainvanhout.optionalrest.scope;

import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.BuildParameters;
import alainvanhout.optionalrest.scope.definition.ScopeDefinition;
import alainvanhout.optionalrest.services.mapping.Mapping;

import java.util.HashMap;
import java.util.Map;

public class BasicScope implements Scope {

    protected String scopeId;
    protected Map<String, Scope> relativeScopes = new HashMap<>();

    @Override
    public Map<String, Object> buildDefinitionMap(int deep, BuildParameters params) {
        return null;
    }

    @Override
    public ScopeDefinition getDefinition() {
        return null;
    }

    @Override
    public Response follow(Request request) {
        return null;
    }

    @Override
    public Scope addPassMapping(Mapping mapping) {
        return null;
    }

    @Override
    public Scope addErrorMapping(Mapping mapping) {
        return null;
    }

    @Override
    public void setInstanceScope(Scope scope) {

    }

    @Override
    public void addRelativeScope(String relative, Scope scope) {
        relativeScopes.put(relative, scope);
    }

    @Override
    public Map<String, Scope> getRelativeScopes() {
        return relativeScopes;
    }

    @Override
    public Scope scopeId(String scopeId) {
        this.scopeId = scopeId;
        return this;
    }

    @Override
    public String getScopeId() {
        return scopeId;
    }
}
