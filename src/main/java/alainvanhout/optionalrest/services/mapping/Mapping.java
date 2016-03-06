package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.Supported;

public interface Mapping {
    Response call(Request request);

    Supported getSupported();
}