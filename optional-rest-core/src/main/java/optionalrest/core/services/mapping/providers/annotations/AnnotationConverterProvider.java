package optionalrest.core.services.mapping.providers.annotations;

import optionalrest.core.services.mapping.providers.ConverterProvider;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface AnnotationConverterProvider extends ConverterProvider<Annotation, AnnotationConverter> {

    @Override
    default boolean checkClass(Map.Entry<Class, AnnotationConverter> entry, Annotation annotation){
        return  annotation.annotationType().equals(entry.getKey());
    }
}
