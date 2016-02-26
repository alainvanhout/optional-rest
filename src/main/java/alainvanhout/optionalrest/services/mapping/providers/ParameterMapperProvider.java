package alainvanhout.optionalrest.services.mapping.providers;

import alainvanhout.optionalrest.request.RestRequest;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ParameterMapperProvider {

    default Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> getParameterMappers(){
        Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> mappers = new HashMap<>();

        Map<Class, BiFunction<Parameter, RestRequest, Object>> mappersForClass = getParameterMappersForClass();
        for (Map.Entry<Class, BiFunction<Parameter, RestRequest, Object>> entry : mappersForClass.entrySet()) {
            mappers.put(p -> p.getType().equals(entry.getKey()), entry.getValue());
        }

        return mappers;
    }

    Map<Class, BiFunction<Parameter, RestRequest, Object>> getParameterMappersForClass();
}
