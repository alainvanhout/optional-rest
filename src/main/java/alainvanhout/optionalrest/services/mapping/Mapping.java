package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.Supported;

import java.util.Collection;
import java.util.Set;

public interface Mapping {
    Response call(Request request);

    Supported getSupported();

    Mapping supported(Supported supported);
}