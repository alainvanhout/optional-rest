package optionalrest.core.scope;

import optionalrest.core.request.Request;

import java.util.HashMap;
import java.util.Map;

public abstract class BasicScope implements Scope {

    protected String scopeId;
    protected Scope parent = null;
    protected Map<String, Scope> relativeScopes = new HashMap<>();
    protected Map<Scope, String> relativePaths = new HashMap<>();

    @Override
    public void addRelativeScope(String relative, Scope scope) {
        if (scope.getParent() == null){
            scope.parent(this);
        }
        relativeScopes.put(relative, scope);
        relativePaths.put(scope, relative);
    }

    @Override
    public Map<String, Scope> getRelativeScopes() {
        return relativeScopes;
    }

    @Override
    public String getRelativePath(Scope scope, Request request){
        if (scope.equals(getInstanceScope())){
            return "{id}";
        }
        if (relativePaths.containsKey(scope)){
            return relativePaths.get(scope);
        }
        return null;
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

    @Override
    public String getFullPath() {
        return getFullPath(null);
    }

    @Override
    public String getFullPath(Request request){
        if (parent != null){
            return parent.getFullPath(request) + "/" + parent.getRelativePath(this, request);
        }
        return "";
    }
}
