package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.ScopeContainer;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ScopeFactory {

    void processContainer(ScopeContainer container, Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> parameterMappers, Map<Class, Function<Object, Object>> responseTypeMappers);
}
