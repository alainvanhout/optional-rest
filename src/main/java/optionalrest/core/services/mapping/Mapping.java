package optionalrest.core.services.mapping;

import optionalrest.core.request.Request;
import optionalrest.core.scope.Scope;
import optionalrest.core.scope.Supported;

import java.util.List;

public interface Mapping {
    void apply(Request request);

    Class getReturnType();

    boolean isPassing();

    int getOrder();

    Supported getSupported();

    List<Scope> getBefore();

    List<Scope> getAfter();
}