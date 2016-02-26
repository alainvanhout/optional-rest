package alainvanhout.optionalrest.services.mapping.providers;

import alainvanhout.optionalrest.request.Headers;
import alainvanhout.optionalrest.request.Parameters;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import org.springframework.stereotype.Service;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Service
public class BasicParameterMapperProvider implements ParameterMapperProvider {

    @Override
    public Map<Class, BiFunction<Parameter, RestRequest, Object>> getParameterMappers() {
        Map<Class, BiFunction<Parameter, RestRequest, Object>> map = new HashMap<>();

        map.put(RestRequest.class, (p, r) -> r);
        map.put(HttpMethod.class, (p, r) -> r.getMethod());
        map.put(Headers.class, (p, r) -> r.getHeaders());
        map.put(Parameters.class, (p, r) -> r.getParameters());

        return map;
    }
}
