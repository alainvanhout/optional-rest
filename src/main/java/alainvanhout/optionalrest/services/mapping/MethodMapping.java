package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.response.RendererResponse;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.response.Response;
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
    private Map<Class, Function<Object, Object>> responseTypeMappers = new HashMap<>();
    private List<Function<RestRequest, Object>> requestMappers;

    public MethodMapping(ScopeContainer container, Method method) {
        this.container = container;
        this.method = method;
    }

    @Override
    public Response call(RestRequest restRequest) {
        try {
            method.setAccessible(true);
            Object[] params = requestMappers.stream().map(m -> m.apply(restRequest)).toArray();
            Object invoke = method.invoke(container, params);
            if (Void.TYPE.equals(method.getReturnType())) {
                return (RendererResponse) invoke;
            } else {
                return processResponse(invoke);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + method.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }
    }

    private Response processResponse(Object response) {
        if (response instanceof RendererResponse) {
            return (RendererResponse) response;
        } else {
            for (Map.Entry<Class, Function<Object, Object>> entry : responseTypeMappers.entrySet()) {
                if (entry.getKey().isAssignableFrom(response.getClass())) {
                    return processResponse(entry.getValue().apply(response));
                }
            }
            throw new RestException("No response mapping available for type " + response.getClass());
        }
    }

    public MethodMapping parameterMappers(Map<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> parameterMappers) {
        this.parameterMappers.putAll(parameterMappers);
        formMappers();
        return this;
    }

    private void formMappers() {
        requestMappers = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            BiFunction<Parameter, RestRequest, Object> mapper = getMapper(parameter);
            requestMappers.add(r -> mapper.apply(parameter, r));
        }
    }

    private BiFunction<Parameter, RestRequest, Object> getMapper(Parameter parameter) {
        for (Map.Entry<Function<Parameter, Boolean>, BiFunction<Parameter, RestRequest, Object>> entry : parameterMappers.entrySet()) {
            if (entry.getKey().apply(parameter)) {
                return entry.getValue();
            }
        }
        throw new RestException("No mapper found for parameter " + parameter);
    }

    public MethodMapping responseTypeMappers(Map<Class, Function<Object, Object>> responseTypeMappers) {
        this.responseTypeMappers.putAll(responseTypeMappers);
        return this;
    }
}
