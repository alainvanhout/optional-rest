package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeManager;

import java.lang.reflect.Field;

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
