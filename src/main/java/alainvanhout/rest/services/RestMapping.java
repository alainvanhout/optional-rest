package alainvanhout.rest.services;

import alainvanhout.rest.RestException;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class RestMapping {

    private Object owner;
    private Field field;
    private Method method;
    private ScopeContainer scopeContainer;
    private RestMappingType type;
    private Supplier<Scope> scopeSupplier;

    public RestMapping(Object owner) {
        this.owner = owner;
    }

    public RestMappingType getType() {
        return type;
    }

    public void setType(RestMappingType type) {
        this.type = type;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Class owner) {
        this.owner = owner;
    }

    public enum RestMappingType {
        FIELD,
        METHOD,
        SCOPE_CONTAINER,
        SCOPE
    }

    public RestMapping field(Field field) {
        this.field = field;
        type = RestMappingType.FIELD;
        return this;
    }

    public RestMapping method(Method method) {
        this.method = method;
        type = RestMappingType.METHOD;
        return this;
    }

    public RestMapping scopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
        type = RestMappingType.SCOPE_CONTAINER;
        return this;
    }

    public RestMapping set(Object object) {
        if (object instanceof Field) {
            return field((Field) object);
        }
        if (object instanceof Method) {
            return method((Method) object);
        }
        if (object instanceof ScopeContainer) {
            return scopeContainer((ScopeContainer) object);
        }
        throw new RestException("Type not supported: " + object.getClass().getName());
    }

    public Field getField() {
        return field;
    }

    public Method getMethod() {
        return method;
    }

    public ScopeContainer getScopeContainer() {
        return scopeContainer;
    }

    public RestMapping setScopeSupplier(Supplier<Scope> scopeSupplier){
        this.scopeSupplier = scopeSupplier;
        if (type == null) {
            type = RestMappingType.SCOPE;
        }
        return this;
    }

    public Scope getScope(){
        if (this.scopeSupplier == null){
            throw new RestException("No scope supplier available");
        }
        return scopeSupplier.get();
    }
}
