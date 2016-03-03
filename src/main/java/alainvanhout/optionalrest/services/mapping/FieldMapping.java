package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeManager;

import java.lang.reflect.Field;

public class FieldMapping extends BasicMapping {

    private ScopeManager scopeManager;
    private ScopeContainer container;
    private Field field;

    public FieldMapping(ScopeManager scopeManager, ScopeContainer container, Field field) {
        this.scopeManager = scopeManager;
        this.container = container;
        this.field = field;
    }

    @Override
    public Response call(Request request) {
        try {
            field.setAccessible(true);
            ScopeContainer target = (ScopeContainer) field.get(container);
            return scopeManager.follow(target, request);
        } catch (IllegalAccessException e) {
            throw new RestException("Encountered error while calling mapping method: "
                    + field.getName() + " for container " + container.getClass().getCanonicalName(), e);
        }

    }
}
