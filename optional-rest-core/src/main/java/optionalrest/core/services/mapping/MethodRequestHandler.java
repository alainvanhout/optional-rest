package optionalrest.core.services.mapping;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.mapping.providers.Evaluator;
import optionalrest.core.services.mapping.providers.parameters.ParameterConverter;
import optionalrest.core.services.mapping.providers.responses.ResponseConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MethodRequestHandler extends BasicRequestHandler {

    private ScopeContainer container;
    private Method method;
    private Map<Evaluator<Parameter>, ParameterConverter> parameterConverters = new HashMap<>();
    private Map<Evaluator<Object>, ResponseConverter> responseConverters = new HashMap<>();
    private List<Function<Request, Object>> requestMappers;

    public MethodRequestHandler(ScopeContainer container, Method method) {
        this.container = container;
        this.method = method;
    }

    @Override
    public Class getReturnType() {
        return method.getReturnType();
    }

    @Override
    public void apply(Request request) {
        Object[] parameters = assembleParameters(request);
        try {
            method.setAccessible(true);
            Object result = method.invoke(container, parameters);
            if (!Void.TYPE.equals(getReturnType())) {
                Response response = processResponse(result);
                request.response(response);
            }
        } catch (IllegalArgumentException e) {
            String parameterTypes = "{" + Arrays.asList(parameters).stream()
                    .map(p -> p.getClass().getCanonicalName()).collect(Collectors.joining(", ")) + "}";

            throw new RestException("Could not call method '" + method.getName()
                    + "' for container '" + container.getClass().getCanonicalName()
                    + "' because parameter types did not match: " + parameterTypes, e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + method.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }
    }

    private Object[] assembleParameters(Request request) {
        return requestMappers.stream().map(m -> m.apply(request)).toArray();
    }

    private Response processResponse(Object response) {
        if (response instanceof Response) {
            return (Response) response;
        } else {
            for (Map.Entry<Evaluator<Object>, ResponseConverter> entry : responseConverters.entrySet()) {
                if (entry.getKey().apply(response)){
                    return processResponse(entry.getValue().apply(response));
                }
            }
            throw new RestException("No response mapping available for type " + response.getClass());
        }
    }

    public MethodRequestHandler parameterMappers(Map<Evaluator<Parameter>, ParameterConverter> parameterMappers) {
        this.parameterConverters.putAll(parameterMappers);
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
        for (Map.Entry<Evaluator<Parameter>, ParameterConverter> entry : parameterConverters.entrySet()) {
            if (entry.getKey().apply(parameter)) {
                return entry.getValue();
            }
        }
        throw new RestException("No mapper found for parameter " + parameter);
    }

    public MethodRequestHandler responseTypeMappers(Map<Evaluator<Object>, ResponseConverter> responseTypeMappers) {
        this.responseConverters.putAll(responseTypeMappers);
        return this;
    }
}
