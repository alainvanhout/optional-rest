package optionalrest.core.services.mapping.providers;

import optionalrest.core.RestException;
import optionalrest.core.request.Headers;
import optionalrest.core.request.Parameters;
import optionalrest.core.request.Request;
import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.services.factories.*;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BasicParameterMapperProvider implements ParameterMapperProvider {

    @Override
    public Map<Class, BiFunction<Parameter, Request, Object>> getParameterMappersForClass() {
        Map<Class, BiFunction<Parameter, Request, Object>> map = new HashMap<>();

        map.put(Request.class, (p, r) -> r);
        map.put(HttpMethod.class, (p, r) -> r.getMethod());
        map.put(Headers.class, (p, r) -> r.getHeaders());
        map.put(Parameters.class, (p, r) -> r.getParameters());
        map.put(RestException.class, (p, r) -> r.getContext().get("exception"));

        return map;
    }

    @Override
    public Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> getParameterMappers() {
        Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> map = new HashMap<>();

        // Parameter to list
        map.put(p -> p.getAnnotation(Param.class) != null && p.getType().isAssignableFrom(List.class), (p, r) -> {
            Param param = p.getAnnotation(Param.class);
            return r.getParameters().get(param.value());
        });
        // Parameter to String
        map.put(p -> p.getAnnotation(Param.class) != null && p.getType().isAssignableFrom(String.class), (p, r) -> {
            Param param = p.getAnnotation(Param.class);
            Parameters parameters = r.getParameters();
            if (parameters.contains(param.value()) && parameters.get(param.value()).size() > 0){
                return parameters.get(param.value()).get(0);
            }
            return null;
        });
        // Header
        map.put(p -> p.getAnnotation(Header.class) != null, (p, r) -> {
            Header header = p.getAnnotation(Header.class);
            return r.getHeaders().get(header.value());
        });
        // FromContext
        map.put(p -> p.getAnnotation(FromContext.class) != null, (p, r) -> {
            FromContext fromContext = p.getAnnotation(FromContext.class);
            return r.getContext().get(fromContext.value());
        });
        // request path step
        map.put(p -> p.getAnnotation(Step.class) != null && p.getType().equals(String.class), (p, r) -> r.getPath().getStep());
        // full request path so far
        map.put(p -> p.getAnnotation(Path.class) != null && p.getType().equals(String.class),
                (p, r) -> r.getPath().getPassedSteps().stream().collect(Collectors.joining("/")));
        // body as inputstream
        map.put(p -> p.getType().isAssignableFrom(InputStream.class), (p, r) -> new ReaderInputStream(r.getReader()));

        return map;
    }
}
