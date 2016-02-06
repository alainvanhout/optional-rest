package alainvanhout.rest.restservice;

import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.utils.JsonUtils;
import alainvanhout.rest.annotations.RestEntity;
import alainvanhout.rest.annotations.RestInstance;
import alainvanhout.rest.annotations.RestRelative;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.utils.ReflectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

@Service
public abstract class RestService {

    private RestMappings restEntityPass = new RestMappings();
    private RestMappings restEntityReturn = new RestMappings();
    private RestMappings restInstancePass = new RestMappings();
    private RestMappings restInstanceReturn = new RestMappings();
    private Map<String, RestMappings> restEntityRelativePass = new HashMap<>();
    private Map<String, RestMappings> restEntityRelativeReturn = new HashMap<>();
    private Map<String, RestMappings> restInstanceRelativePass = new HashMap<>();
    private Map<String, RestMappings> restInstanceRelativeReturn = new HashMap<>();

    @PostConstruct
    public void setup() {
        try {
            for (Method method : this.getClass().getDeclaredMethods()) {
                checkForMapping(method, method.getReturnType().equals(Void.TYPE));
            }

            for (Field field : this.getClass().getDeclaredFields()) {
                checkForMapping(field, false);
            }

        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + this.getClass(), e);
        }
    }

    private void checkForMapping(AccessibleObject accessibleObject, boolean passing) {
        RestInstance restInstance = ReflectionUtils.retrieveAnnotation(accessibleObject, RestInstance.class);
        RestEntity restEntity = ReflectionUtils.retrieveAnnotation(accessibleObject, RestEntity.class);
        RestRelative restRelative = ReflectionUtils.retrieveAnnotation(accessibleObject, RestRelative.class);

        if (restInstance != null) {
            addMapping(accessibleObject, restInstance.methods(), passing ? restInstancePass : restInstanceReturn);
        }
        if (restEntity != null) {
            addMapping(accessibleObject, restEntity.methods(), passing ? restEntityPass : restEntityReturn);
        }
        if (restRelative != null) {
            Map<String, RestMappings> restRelativeMap = null;
            if (restRelative.forInstance()) {
                restRelativeMap = passing ? restInstanceRelativePass : restInstanceRelativeReturn;
            } else {
                restRelativeMap = passing ? restEntityRelativePass : restEntityRelativeReturn;
            }
            String value = restRelative.value();
            if (!restRelativeMap.containsKey(value)) {
                restRelativeMap.put(value, new RestMappings());
            }
            addMapping(accessibleObject, restRelative.methods(), restRelativeMap.get(value));
        }
    }

    private void addMapping(AccessibleObject mappingParent, HttpMethod[] methods, RestMappings mappings) {
        String name = ReflectionUtils.retrieveName(mappingParent);
        for (HttpMethod method : methods) {
            if (mappings.contains(method)) {
                throw new RestException("Mapping already set for method " + method);
            }
            mappings.addMapping(method, new RestMapping(name, ReflectionUtils.retrieveType(mappingParent)));
        }
    }

    private RestResponse call(AccessibleObject accessibleObject, RestRequest restRequest) {
        accessibleObject.setAccessible(true);
        try {
            if (accessibleObject instanceof Method) {
                return (RestResponse) ((Method) accessibleObject).invoke(this, restRequest);
            } else if (accessibleObject instanceof Field) {
                RestService target = (RestService) ((Field) accessibleObject).get(this);
                return target.follow(restRequest);
            } else {
                throw new RestException("Type of accessible object not supported: " + accessibleObject.getClass());
            }
        } catch (IllegalAccessException e) {
            throw new RestException("Method call unsuccessful", e);
        } catch (InvocationTargetException e) {
            throw new RestException("Method call unsuccessful", e);
        }
    }

    private RestResponse call(RestMappings mappings, RestRequest restRequest) {
        return call(ReflectionUtils.retrieveAccessibleObject(this.getClass(), mappings.get(restRequest.getMethod())), restRequest);
    }

    private RestResponse followEntityRelative(String step, RestRequest restRequest) {
        // passing entity relative
        if (restEntityRelativePass.containsKey(step) && restEntityRelativePass.get(step).contains(restRequest.getMethod())) {
            call(restEntityRelativePass.get(step), restRequest);
        }

        // arriving at entity relative
        if (restRequest.getPath().done() && restEntityRelativeReturn.containsKey(step)) {
            return call(restEntityRelativeReturn.get(step), restRequest);
        }

        throw new RestException("Not entity relative arrive mapping found");
    }

    public RestResponse follow(RestRequest restRequest) {
        return followEntity(restRequest);
    }

    private RestResponse followEntity(RestRequest restRequest) {
        // passing entity
        if (restEntityPass.contains(restRequest.getMethod())) {
            call(restEntityPass, restRequest);
        }

        // arriving at entity
        if (restRequest.getPath().done()) {
            return followEntityArrive(restRequest);
        }

        // continuing to entity relative or instance
        if (restEntityRelativePass.containsKey(restRequest.getPath().peekStep())) {
            call(restEntityRelativePass.get(restRequest.getPath().peekStep()), restRequest);
        }

        // arriving at entity relative
        if (restEntityRelativeReturn.containsKey(restRequest.getPath().peekStep())) {
            String step = restRequest.getPath().nextStep();
            return followEntityRelative(step, restRequest);
        }

        // continuing with instance
        return followInstance(restRequest);
    }

    private RestResponse followInstance(RestRequest restRequest) {
        restRequest.getPath().nextStep();

        if (restInstancePass.contains(restRequest.getMethod())) {
            call(restInstancePass, restRequest);
        }

        if (restRequest.done()) {
            return callInstanceArrive(restRequest);
        }

        return followInstanceRelative(restRequest);
    }

    private RestResponse followInstanceRelative(RestRequest restRequest) {
        String step = restRequest.getPath().nextStep();

        // pass instance relative
        if (restInstanceRelativePass.containsKey(step)) {
            call(restInstanceRelativePass.get(step), restRequest);
        }

        // arrive at instance relative
        if (restRequest.done() && restInstanceRelativeReturn.containsKey(step) && restInstanceRelativeReturn.get(step).contains(restRequest.getMethod())) {
            return call(restInstanceRelativeReturn.get(step), restRequest);
        }

        throw new RestException("Not instance relative return mapping found");
    }

    private RestResponse callInstanceArrive(RestRequest restRequest) {
        // TODO inject instance identifier into method call
        if (restInstanceReturn.contains(restRequest.getMethod())) {
            return call(restInstanceReturn, restRequest);
        }

        if (HttpMethod.OPTIONS.equals(restRequest.getMethod())) {
            Class entityClass = getEntityClass();
            if (entityClass != null) {
                String json = JsonUtils.entityToJson(entityClass);
                return new RestResponse().renderer(new StringRenderer(json));
            }
        }

        throw new RestException("No instance arrive mapping defined");
    }

    private RestResponse followEntityArrive(RestRequest restRequest) {
        if (restEntityReturn.contains(restRequest.getMethod())) {
            return call(restEntityReturn, restRequest);
        }

        if (HttpMethod.OPTIONS.equals(restRequest.getMethod())) {
            Class entityClass = getEntityClass();
            if (entityClass != null) {
                String json = JsonUtils.entityToJson(entityClass);
                return new RestResponse().renderer(new StringRenderer(json));
            }
        }

        throw new RestException("No entity arrive mapping defined");
    }

    public Class getEntityClass() {
        return null;
    }
}
