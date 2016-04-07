package optionalrest.core.scope;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.ScopeDefinition;
import optionalrest.core.services.mapping.RequestHandler;
import optionalrest.core.services.mapping.Mappings;

import java.util.Collection;
import java.util.List;

import static optionalrest.core.request.meta.HttpMethod.OPTIONS;

public class GenericScope extends BasicScope {

    private transient ScopeDefinition definition = new ScopeDefinition();

    private transient Mappings passMappings = new Mappings();
    private transient Mappings errorMappings = new Mappings();

    private transient Scope instanceScope;

    private transient OptionsRequestHandler optionsRequestHandler = null;

    @Override
    public Response follow(Request request) {
        try {
            // always run passing mappings (some may involve setting the response)
            pass(request);

            // whether arrived or not, if request is done, return response
            if (request.isDone()){
                return request.getResponse();
            }

            // arriving at scope
            if (request.getPath().isArrived()) {
                return arrive(request);
            }

            // continue with request
            return proceed(request);

        } catch (Exception e) {
            List<RequestHandler> errorRequestHandler = errorMappings.getMappings();
            if (!errorRequestHandler.isEmpty()) {
                request.getContext().add("exception", e);
                return apply(errorRequestHandler, request);
            }
            throw e;
        }

    }

    @Override
    public void pass(Request request) {
        List<RequestHandler> passing = passMappings.getMappings(request, true);
        apply(passing, request);
    }

    @Override
    public Response arrive(Request request) {
        // run arrive mappings
        List<RequestHandler> arriving = passMappings.getMappings(request, false);
        apply(arriving, request);

        // arrived with response
        if (request.hasResponse()) {
            return request.getResponse();
        }

        // default OPTIONS response
        if (OPTIONS.equals(request.getMethod()) && optionsRequestHandler!= null) {
            return optionsRequestHandler.get(request, this);
        }

        throw new RestException("No response was set");
    }

    public Response proceed(Request request) {
        String step = request.getPath().nextStep();

        // first check relative scopes
        if (relativeScopes.containsKey(step)) {
            return apply(relativeScopes.get(step), request);
        }

        // then check fallback scope
        if (instanceScope != null) {
            return instanceScope.follow(request);
        }

        throw new RestException("No appropriate mapping found for scope " + this.getClass().getSimpleName());
    }

    private Response apply(Scope scope, Request request) {
        return scope.follow(request);
    }

    private Response apply(Collection<RequestHandler> requestHandlers, Request request) {
        for (RequestHandler requestHandler : requestHandlers) {
            apply(requestHandler, request);
            if (request.isDone()) {
                break;
            }
        }
        return request.getResponse();
    }

    private void apply(RequestHandler requestHandler, Request request) {
        try {
            for (Scope scope : requestHandler.getBefore()) {
                scope.pass(request);
            }

            requestHandler.apply(request);

            for (Scope scope : requestHandler.getAfter()) {
                scope.pass(request);
            }
        } catch (RestException e) {
            e.add("requestHandler", requestHandler);
            throw e;
        } catch (Exception e) {
            if (e.getCause() instanceof RestException) {
                throw ((RestException) e.getCause()).add("requestHandler", requestHandler);
            }
            throw new RestException("Unable to call request handler", e).add("requestHandler", requestHandler);
        }
    }

    @Override
    public ScopeDefinition getDefinition() {
        return definition;
    }

    @Override
    public GenericScope addRequestHandler(RequestHandler requestHandler) {
        return addRequestHandler(passMappings, requestHandler);
    }

    @Override
    public GenericScope addErrorRequestHandler(RequestHandler requestHandler) {
        return addRequestHandler(errorMappings, requestHandler);
    }

    private GenericScope addRequestHandler(Mappings mappings, RequestHandler requestHandler) {
        mappings.add(requestHandler);
        return this;
    }

    @Override
    public void setInstanceScope(Scope scope) {
        if (scope.getParent() == null){
            scope.parent(this);
        }
        this.instanceScope = scope;
    }

    @Override
    public String toString() {
        return scopeId;
    }

    @Override
    public GenericScope scopeId(String scopeId) {
        this.scopeId = scopeId;
        return this;
    }

    @Override
    public String getScopeId() {
        return scopeId;
    }

    @Override
    public GenericScope optionsRequestHandler(OptionsRequestHandler optionsRequestHandler) {
        this.optionsRequestHandler = optionsRequestHandler;
        return this;
    }

    @Override
    public Supported getSupported() {
        return passMappings.getSupported();
    }

    @Override
    public Scope getInstanceScope() {
        return instanceScope;
    }
}
