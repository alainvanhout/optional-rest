package alainvanhout.rest.utils;

import alainvanhout.rest.RestException;
import alainvanhout.rest.services.RestMapping;

import java.lang.reflect.*;

public class ReflectionUtils {
    public static <T> T retrieveAnnotation(AccessibleObject parent, Class annotationClass) {
        Object[] pathAnnotations = parent.getAnnotationsByType(annotationClass);
        if (pathAnnotations.length == 1) {
            return (T) pathAnnotations[0];
        }
        return null;
    }

    public static <T> T retrieveAnnotation(Class parent, Class annotationClass) {
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
