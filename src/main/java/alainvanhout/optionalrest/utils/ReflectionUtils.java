package alainvanhout.optionalrest.utils;

import java.lang.reflect.AccessibleObject;

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
}
