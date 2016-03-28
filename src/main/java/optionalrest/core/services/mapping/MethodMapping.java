package optionalrest.core.services.mapping;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.ScopeContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MethodMapping extends BasicMapping {

    private ScopeContainer container;
    private Method method;
    private Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> parameterMappers = new HashMap<>();
    private Map<Class, Function<Object, Object>> responseTypeMappers = new HashMap<>();
    private List<Function<Request, Object>> requestMappers;

    public MethodMapping(ScopeContainer container, Method method) {
        this.container = container;
        this.method = method;
    }

    @Override
    public Class getReturnType() {
        return method.getReturnType();
    }

    @Override
    public void apply(Request request) {
        try {
            method.setAccessible(true);
            Object result = method.invoke(container, assembleParameters(request));
            if (!Void.TYPE.equals(getReturnType())) {
                Response response = processResponse(result);
                request.response(response);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + method.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }
    }

    public Object[] assembleParameters(Request request) {
        return requestMappers.stream().map(m -> m.apply(request)).toArray();
    }

    private Response processResponse(Object response) {
        if (response instanceof Response) {
            return (Response) response;
        } else {
            for (Map.Entry<Class, Function<Object, Object>> entry : responseTypeMappers.entrySet()) {
                if (entry.getKey().isAssignableFrom(response.getClass())) {
                    return processResponse(entry.getValue().apply(response));
                }
            }
            throw new RestException("No response mapping available for type " + response.getClass());
        }
    }

    public MethodMapping parameterMappers(Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> parameterMappers) {
        this.parameterMappers.putAll(parameterMappers);
        formMappers();
        return this;
    }

    private void formMappers() {
        requestMappers = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            BiFunction<Parameter, Request, Object> mapper = getMapper(parameter);
            requestMappers.add(r -> mapper.apply(parameter, r));
        }
    }

    private BiFunction<Parameter, Request, Object> getMapper(Parameter parameter) {
        for (Map.Entry<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> entry : parameterMappers.entrySet()) {
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
