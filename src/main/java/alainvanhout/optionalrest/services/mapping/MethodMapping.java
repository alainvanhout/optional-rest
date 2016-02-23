package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodMapping implements Mapping {

    private ScopeContainer container;
    private Method method;

    public MethodMapping(ScopeContainer container, Method method) {
        this.container = container;
        this.method = method;
    }

    @Override
    public RestResponse call(RestRequest restRequest) {
        try {
            method.setAccessible(true);
            return (RestResponse) method.invoke(container, restRequest);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + method.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }
    }
}
