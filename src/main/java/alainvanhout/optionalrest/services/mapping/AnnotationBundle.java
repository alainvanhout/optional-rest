package alainvanhout.optionalrest.services.mapping;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationBundle {

    private static final int MAX_DEPTH = 15;
    public static final String JAVA_LANG_ANNOTATION = "java.lang.annotation";
    List<Annotation> list = new ArrayList<>();

    public void add(Annotation[] annotations) {
        addForDepth(annotations, list, MAX_DEPTH);
    }

    private void addForDepth(Annotation[] annotations, List<Annotation> list, int depth) {
        if (depth >= 0) {
            for (Annotation annotation : annotations) {
                String canonicalName = annotation.annotationType().getCanonicalName();
                if (!StringUtils.startsWith(canonicalName, JAVA_LANG_ANNOTATION)) {
                    list.add(annotation);
                    addForDepth(annotation.annotationType().getAnnotations(), list, depth - 1);
                }
            }
        }
    }

    public boolean contains(Class clazz) {
        return list.stream().anyMatch(c -> clazz.equals(c.annotationType()));
    }

    public List<Annotation> subList(Class clazz) {
        return list.stream().filter(c -> clazz.equals(c.annotationType())).collect(Collectors.toList());
    }

}
