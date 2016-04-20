package optionalrest.core.services.mapping.providers.annotations;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Function;

public interface AnnotationConverter extends Function<Annotation, Collection<Annotation>> {
}
