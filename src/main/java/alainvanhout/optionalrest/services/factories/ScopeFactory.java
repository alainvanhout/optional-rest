package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.BiFunction;

public interface ScopeFactory {

    void processContainer(ScopeContainer container, Map<Class, BiFunction<Parameter, RestRequest, Object>> parameterMappers);
}
