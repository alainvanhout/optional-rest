package optionalrest.core.services.mapping;

import optionalrest.core.request.Request;
import optionalrest.core.scope.Supported;

public interface Mapping {
    void apply(Request request);

    Class getReturnType();

    boolean isPassing();

    int getOrder();

    Supported getSupported();
}