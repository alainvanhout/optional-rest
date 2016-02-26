package alainvanhout.optionalrest.services.mapping.providers;

import alainvanhout.optionalrest.request.RestRequest;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.BiFunction;

public interface ParameterMapperProvider {

    Map<Class, BiFunction<Parameter, RestRequest, Object>> getParameterMappers();
}
