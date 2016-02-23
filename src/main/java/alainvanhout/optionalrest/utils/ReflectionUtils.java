package alainvanhout.optionalrest.utils;

import alainvanhout.optionalrest.RestException;

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
