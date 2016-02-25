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
import java.util.function.Function;

public class MethodMapping implements Mapping {

    private ScopeContainer container;
    private Method method;
    private Map<Class, Function<RestRequest, Object>> parameterMappers = new HashMap<>();
    private List<Function<RestRequest, Object>> mappers;

    public MethodMapping(ScopeContainer container, Method method) {
        this.container = container;
        this.method = method;
    }

    @Override
    public RestResponse call(RestRequest restRequest) {
        if (mappers == null) {
            formMappers();
        }
        try {
            method.setAccessible(true);
            Object[] params = mappers.stream().map(m -> m.apply(restRequest)).toArray();
            return (RestResponse) method.invoke(container, params);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + method.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }
    }

    public MethodMapping parameterMappers(Map<Class, Function<RestRequest, Object>> parameterMappers) {
        this.parameterMappers.putAll(parameterMappers);
        return this;
    }

    private void formMappers() {
        mappers = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            if (parameterMappers.containsKey(parameter.getType())) {
                mappers.add(parameterMappers.get(parameter.getType()));
            } else {
                throw new RestException("Method parameter type not suppported: " + parameter.getType());
            }
        }
    }
}
