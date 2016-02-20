package alainvanhout.rest.services.mapping;

import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.services.ScopeManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class FieldMapping implements Mapping {

    private ScopeManager scopeManager;
    private ScopeContainer container;
    private Field field;

    public FieldMapping(ScopeManager scopeManager, ScopeContainer container, Field field) {
        this.scopeManager = scopeManager;
        this.container = container;
        this.field = field;
    }

    @Override
    public RestResponse call(RestRequest restRequest) {
        try {
            field.setAccessible(true);
            ScopeContainer target = (ScopeContainer) field.get(container);
            return scopeManager.follow(target, restRequest);
        } catch (IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + field.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }

    }
}
