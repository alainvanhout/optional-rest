package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MethodMapping implements Mapping {

    private ScopeContainer container;
    private Method method;
    private Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> parameterMappers = new HashMap<>();
    private List<Function<RestRequest, Object>> mappers;

    public MethodMapping(ScopeContainer container, Method method) {
        this.container = container;
        this.method = method;
    }

    @Override
    public RestResponse call(RestRequest restRequest) {
        try {
            method.setAccessible(true);
            Object[] params = mappers.stream().map(m -> m.apply(restRequest)).toArray();
            return (RestResponse) method.invoke(container, params);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + method.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }
    }

    public MethodMapping parameterMappers(Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> parameterMappers) {
        this.parameterMappers.putAll(parameterMappers);
        formMappers();
        return this;
    }

    private void formMappers() {
        mappers = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            BiFunction<Parameter, RestRequest, Object> mapper = getMapper(parameter);
            mappers.add(r -> mapper.apply(parameter, r));
        }
    }

    private BiFunction<Parameter, RestRequest, Object> getMapper(Parameter parameter) {
        for (Map.Entry<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> entry: parameterMappers.entrySet()) {
            if (entry.getKey().apply(parameter)) {
                return entry.getValue();
            }
        }
        throw new RestException("No mapper found for parameter " + parameter);
    }
}
