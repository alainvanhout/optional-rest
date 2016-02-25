package alainvanhout.optionalrest.services.mapping.providers;

import alainvanhout.optionalrest.request.RestRequest;

import java.util.Map;
import java.util.function.Function;

public interface ParameterMapperProvider {

    Map<Class, Function<RestRequest, Object>> getParameterMappers();
}
