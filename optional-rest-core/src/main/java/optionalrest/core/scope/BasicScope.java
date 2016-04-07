package optionalrest.core.scope;

import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.ScopeDefinition;
import optionalrest.core.services.mapping.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicScope implements Scope {

    protected String scopeId;
    protected Scope parent = null;
    protected Map<String, Scope> relativeScopes = new HashMap<>();

    @Override
    public ScopeDefinition getDefinition() {
        return null;
    }

    @Override
    public Response follow(Request request) {
        return null;
    }

    @Override
    public Scope addRequestHandler(RequestHandler requestHandler) {
        return null;
    }

    @Override
    public Scope addErrorRequestHandler(RequestHandler requestHandler) {
        return null;
    }

    @Override
    public void setInstanceScope(Scope scope) {

    }

    @Override
    public void addRelativeScope(String relative, Scope scope) {
        if (scope.getParent() == null){
            scope.parent(this);
        }
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

    @Override
    public Scope getParent() {
        return parent;
    }

    @Override
    public Scope parent(Scope parent) {
        this.parent = parent;
        return this;
    }
}
