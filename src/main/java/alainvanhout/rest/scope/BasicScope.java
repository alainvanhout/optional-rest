package alainvanhout.rest.scope;

import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.restservice.RestMappings;
import alainvanhout.rest.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BasicScope implements Scope {

    private RestMappings passMappings = new RestMappings();
    private RestMappings arriveMappings = new RestMappings();
    private Map<String, Scope> relativeScopes = new HashMap<>();
    private RestMappings fallbackMappings = new RestMappings();

    @Override
    public RestResponse follow(RestRequest restRequest) {
        // always call passing scope
        if (passMappings.contains(restRequest.getMethod())) {
            call(passMappings, restRequest);
        }

        // arriving at scope
        if (restRequest.getPath().done() && arriveMappings.contains(restRequest.getMethod())) {
            return  call(arriveMappings, restRequest);
        }

        // not yet arrived
        String step = restRequest.getPath().nextStep();

        // first check relative scopes
        if (relativeScopes.containsKey(step)){
            return relativeScopes.get(step).follow(restRequest);
        }

        // then use fallback mapping
        if (fallbackMappings.contains(restRequest.getMethod())){
            return call(fallbackMappings, restRequest);
        }

        throw new RestException("No appropriate mapping found for scope " + this.getClass().getSimpleName());
    }

    private RestResponse call(RestMappings mappings, RestRequest restRequest) {
        return call(ReflectionUtils.retrieveAccessibleObject(this.getClass(), mappings.get(restRequest.getMethod())), restRequest);
    }

    private RestResponse call(AccessibleObject accessibleObject, RestRequest restRequest) {
        accessibleObject.setAccessible(true);
        try {
            if (accessibleObject instanceof Method) {
                return (RestResponse) ((Method) accessibleObject).invoke(this, restRequest);
            } else if (accessibleObject instanceof Field) {
                Scope target = (Scope) ((Field) accessibleObject).get(this);
                return target.follow(restRequest);
            } else {
                throw new RestException("Type of accessible object not supported: " + accessibleObject.getClass());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RestException("Call unsuccessful", e);
        }
    }
}
