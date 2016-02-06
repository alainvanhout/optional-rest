package alainvanhout.rest.utils;

import alainvanhout.business.Person;
import alainvanhout.rest.RestException;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.restservice.RestMapping;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtils {
    public static <T> T retrieveAnnotation(AccessibleObject parent, Class annotationClass) {
        Object[] pathAnnotations = parent.getAnnotationsByType(annotationClass);
        if (pathAnnotations.length == 1) {
            return (T) pathAnnotations[0];
        }
        return null;
    }

    public static RestMapping.RestMappingType retrieveType(AccessibleObject mappingParent) {
        if (mappingParent instanceof Field) {
            return RestMapping.RestMappingType.FIELD;
        }
        if (mappingParent instanceof Method) {
            return RestMapping.RestMappingType.METHOD;
        }
        throw new RestException("RestMappingType does not support " + mappingParent.getClass().getName());
    }

    public static AccessibleObject retrieveAccessibleObject(Class clazz, RestMapping mapping) {
        try {
            switch (mapping.getType()) {
                case FIELD:
                    return clazz.getDeclaredField(mapping.getAccessibleName());
                case METHOD:
                    return clazz.getMethod(mapping.getAccessibleName(), RestRequest.class);
                default:
                    throw new RestException("Unsupported type:" + mapping.getType());
            }
        } catch (NoSuchFieldException e) {
            throw new RestException("Could not retrieve accesssible object", e);
        } catch (NoSuchMethodException e) {
            throw new RestException("Could not retrieve accesssible object", e);
        }
    }

    public static String retrieveName(AccessibleObject mappingParent) {
        if (mappingParent instanceof Member) {
            return ((Member) mappingParent).getName();
        } else if (mappingParent instanceof Executable) {
            return ((Executable) mappingParent).getName();
        } else {
            throw new RestException("Cannot retrieve name from mappingParent");
        }
    }
}
