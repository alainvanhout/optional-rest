package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;

import java.util.Map;
import java.util.function.Function;

public interface ScopeFactory {

    void processContainer(ScopeContainer container, Map<Class, Function<RestRequest, Object>> parameterMappers);
}
