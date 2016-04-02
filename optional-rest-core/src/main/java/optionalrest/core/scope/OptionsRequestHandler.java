package optionalrest.core.scope;

import optionalrest.core.request.Request;
import optionalrest.core.response.Response;

public interface OptionsRequestHandler {
    Response get(Request request, Scope scope);
}
