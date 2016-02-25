package alainvanhout.optionalrest.services.mapping.providers;

import alainvanhout.optionalrest.request.Headers;
import alainvanhout.optionalrest.request.Parameters;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class BasicParameterMapperProvider implements ParameterMapperProvider {

    @Override
    public Map<Class, Function<RestRequest, Object>> getParameterMappers() {
        Map<Class, Function<RestRequest, Object>> map = new HashMap<>();

        map.put(RestRequest.class, r -> r);
        map.put(HttpMethod.class, r -> r.getMethod());
        map.put(Headers.class, r -> r.getHeaders());
        map.put(Parameters.class, r -> r.getParameters());

        return map;
    }
}
