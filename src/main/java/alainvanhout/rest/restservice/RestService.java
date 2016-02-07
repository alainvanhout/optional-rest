package alainvanhout.rest.restservice;

import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.*;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.BasicScope;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.utils.ReflectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Service
public abstract class RestService implements Scope {
    private BasicScope entityScope = new BasicScope();
    private BasicScope instanceScope = new BasicScope();

    @PostConstruct
    public void setup() {
        entityScope.setFallbackScope(instanceScope);

        try {
            for (Method method : this.getClass().getDeclaredMethods()) {
                checkForMapping(this, method, method.getReturnType().equals(Void.TYPE));
            }

            for (Field field : this.getClass().getDeclaredFields()) {
                checkForMapping(this, field, false);
            }
        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + this.getClass(), e);
        }
    }

    private void checkForMapping(Object owner, AccessibleObject accessibleObject, boolean passing) {
        RestEntity restEntity = ReflectionUtils.retrieveAnnotation(accessibleObject, RestEntity.class);
        RestRelative restRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestRelative.class);
        RestInstance restInstance = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstance.class);
        RestInstanceRelative restInstanceRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstanceRelative.class);
        RestError restError = ReflectionUtils.retrieveAnnotation(accessibleObject, RestError.class);

        if (restEntity != null) {
            addMapping(owner, accessibleObject, this.entityScope, restEntity.methods(), passing);
        }
        if (restRelative != null) {
            String value = restRelative.value();
            addRelativeMapping(owner, accessibleObject, this.entityScope, restRelative.methods(), passing, value);
        }

        if (restInstance != null) {
            addMapping(owner, accessibleObject, this.instanceScope, restInstance.methods(), passing);
        }
        if (restInstanceRelative != null) {
            String value = restInstanceRelative.value();
            addRelativeMapping(owner, accessibleObject, this.instanceScope, restInstanceRelative.methods(), passing, value);
        }

        if (restError != null) {
            addErrorMapping(owner, accessibleObject, this.entityScope, restError.methods(), passing);
            addErrorMapping(owner, accessibleObject, this.instanceScope, restError.methods(), passing);
        }
    }

    private void addErrorMapping(Object owner, AccessibleObject accessibleObject, Scope scope, HttpMethod[] methods, boolean passing) {
        String name = ReflectionUtils.retrieveName(accessibleObject);
        RestMapping restMapping = new RestMapping(owner, name, ReflectionUtils.retrieveType(accessibleObject));
        scope.addErrorMapping(restMapping, methods);
    }

    private void addMapping(Object owner, AccessibleObject accessibleObject, Scope scope, HttpMethod[] methods, boolean passing) {
        String name = ReflectionUtils.retrieveName(accessibleObject);
        RestMapping restMapping = new RestMapping(owner, name, ReflectionUtils.retrieveType(accessibleObject));
        if (passing) {
            scope.addPassMapping(restMapping, methods);
        } else {
            scope.addArriveMapping(restMapping, methods);
        }
    }

    private void addRelativeMapping(Object owner, AccessibleObject accessibleObject, BasicScope scope, HttpMethod[] methods, boolean passing, String relative) {
        Scope relativeScope = scope.getRelativeScope(relative, true);
        addMapping(owner, accessibleObject, relativeScope, methods, passing);
    }

    public RestResponse follow(RestRequest restRequest) {
        return entityScope.follow(restRequest);
    }

    @Override
    public BasicScope addPassMapping(RestMapping mapping, HttpMethod... methods) {
        return null;
    }

    @Override
    public BasicScope addArriveMapping(RestMapping mapping, HttpMethod... methods) {
        return null;
    }

    @Override
    public BasicScope addFallbackMapping(RestMapping mapping, HttpMethod... methods) {
        return null;
    }

    @Override
    public BasicScope addErrorMapping(RestMapping mapping, HttpMethod... methods) {
        return null;
    }

    public Class getEntityClass() {
        return null;
    }
}
