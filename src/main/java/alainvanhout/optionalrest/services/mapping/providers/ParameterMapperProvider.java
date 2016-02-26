package alainvanhout.optionalrest.services.mapping.providers;

import alainvanhout.optionalrest.request.RestRequest;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ParameterMapperProvider {

    default Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> getCombinedParameterMappers(){
        Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> combinedMappers = new HashMap<>();

        Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> mappers = getParameterMappers();
        combinedMappers.putAll(mappers);

        Map<Class, BiFunction<Parameter, RestRequest, Object>> mappersForClass = getParameterMappersForClass();
        for (Map.Entry<Class, BiFunction<Parameter, RestRequest, Object>> entry : mappersForClass.entrySet()) {
            combinedMappers.put(p -> p.getType().equals(entry.getKey()), entry.getValue());
        }

        return combinedMappers;
    }

    default Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> getParameterMappers(){
        return new HashMap<>();
    }

    default Map<Class, BiFunction<Parameter, RestRequest, Object>> getParameterMappersForClass(){
        return new HashMap<>();
    }
}
