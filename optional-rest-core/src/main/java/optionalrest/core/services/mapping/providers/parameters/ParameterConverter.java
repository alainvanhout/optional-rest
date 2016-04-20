package optionalrest.core.services.mapping.providers.parameters;

import optionalrest.core.request.Request;

import java.lang.reflect.Parameter;
import java.util.function.BiFunction;

public interface ParameterConverter extends BiFunction<Parameter, Request, Object> {
}
