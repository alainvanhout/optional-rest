package optionalrest.core.scope;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.ScopeDefinition;
import optionalrest.core.services.mapping.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class LoadBalancingScope extends BasicScope {

    private List<Scope> scopes = new ArrayList<>();

    // just do round robin for now
    private int index = 0;

    public LoadBalancingScope add(Scope scope){
        scopes.add(scope);
        return this;
    }

    @Override
    public void pass(Request request) {
        follow(request);
    }

    @Override
    public Response arrive(Request request) {
        return follow(request);
    }

    @Override
    public ScopeDefinition getDefinition() {
        return null;
    }

    @Override
    public Response follow(Request request) {
        if (scopes.isEmpty()){
            throw new RestException("Cannot perform load balancing on empty list of scopes");
        }

        // move value
        index++;
        if (index >= scopes.size()){
            index = 0;
        }

        return scopes.get(index).follow(request);
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
    public GenericScope optionsRequestHandler(OptionsRequestHandler optionsRequestHandler) {
        return null;
    }

    @Override
    public Supported getSupported() {
        return null;
    }

    @Override
    public Scope getInstanceScope() {
        return null;
    }
}
