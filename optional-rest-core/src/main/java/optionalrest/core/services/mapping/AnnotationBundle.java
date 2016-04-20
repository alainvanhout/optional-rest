package optionalrest.core.services.mapping;

import optionalrest.core.services.mapping.providers.Evaluator;
import optionalrest.core.services.mapping.providers.annotations.AnnotationConverter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationBundle {

    private static final int MAX_DEPTH = 15;
    public static final String JAVA_LANG_ANNOTATION = "java.lang.annotation";
    private final Map<Evaluator<Annotation>, AnnotationConverter> annotationConverters;
    private List<Annotation> list = new ArrayList<>();

    public AnnotationBundle(Map<Evaluator<Annotation>, AnnotationConverter> annotationConverters) {
        this.annotationConverters = annotationConverters;
    }

    public boolean contains(Class clazz) {
        return list.stream().anyMatch(c -> clazz.isInstance(c.annotationType()));
    }

    public boolean containsType(Class clazz) {
        return list.stream().anyMatch(a -> a.annotationType().equals(clazz));
    }

    public List<Annotation> subList(Class clazz) {
        return list.stream()
                .filter(c -> {
                    return clazz.isAssignableFrom(c.annotationType());
                })
                .collect(Collectors.toList());
    }

    public void add(Annotation[] annotations) {
        addForDepth(annotations, list, MAX_DEPTH);
    }

    private void addForDepth(Annotation[] annotations, List<Annotation> list, int depth) {
        if (depth >= 0) {
            for (Annotation annotation : annotations) {
                String canonicalName = annotation.annotationType().getCanonicalName();
                if (!StringUtils.startsWith(canonicalName, JAVA_LANG_ANNOTATION)) {
                    list.addAll(process(annotation));
                    addForDepth(annotation.annotationType().getAnnotations(), list, depth - 1);
                }
            }
        }
    }

    private Collection<Annotation> process(Annotation annotation) {
        if (annotationConverters != null) {
            for (Map.Entry<Evaluator<Annotation>, AnnotationConverter> entry : annotationConverters.entrySet()) {
                if (entry.getKey().apply(annotation)) {
                    return entry.getValue().apply(annotation);
                }
            }
        }
        return Collections.singleton(annotation);
    }
}
