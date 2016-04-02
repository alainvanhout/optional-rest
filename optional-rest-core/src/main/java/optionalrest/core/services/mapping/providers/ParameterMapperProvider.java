package optionalrest.core.services.mapping.providers;

import optionalrest.core.request.Request;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ParameterMapperProvider {

    default Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> getCombinedParameterMappers(){
        Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> combinedMappers = new HashMap<>();

        Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> mappers = getParameterMappers();
        combinedMappers.putAll(mappers);

        Map<Class, BiFunction<Parameter, Request, Object>> mappersForClass = getParameterMappersForClass();
        for (Map.Entry<Class, BiFunction<Parameter, Request, Object>> entry : mappersForClass.entrySet()) {
            combinedMappers.put(p -> p.getType().equals(entry.getKey()), entry.getValue());
        }

        return combinedMappers;
    }

    default Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> getParameterMappers(){
        return new HashMap<>();
    }

    default Map<Class, BiFunction<Parameter, Request, Object>> getParameterMappersForClass(){
        return new HashMap<>();
    }
}
