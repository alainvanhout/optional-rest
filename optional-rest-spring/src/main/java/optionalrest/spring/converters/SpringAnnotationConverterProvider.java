package optionalrest.spring.converters;

import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.services.mapping.providers.Evaluator;
import optionalrest.core.services.mapping.providers.annotations.AnnotationConverter;
import optionalrest.core.services.mapping.providers.annotations.AnnotationConverterProvider;
import optionalrest.core.services.mapping.providers.annotations.HandleBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class SpringAnnotationConverterProvider implements AnnotationConverterProvider {

    @Override
    public Map<Class, AnnotationConverter> defineConvertersForClass() {
        Map<Class, AnnotationConverter> map = new HashMap<>();

        map.put(RequestMapping.class, p -> {
            RequestMapping rm = (RequestMapping) p;
            return Arrays.asList(
                    new HandleBuilder()
                            .methods(Arrays.asList(rm.method()).stream()
                                    .map(r -> HttpMethod.valueOf(r.name()))
                                    .toArray(HttpMethod[]::new))
                            .accept(rm.produces())
                            .contentType(rm.consumes())
                            .build(),
                    HandleBuilder.MARKER);
        });

        return map;
    }

    @Override
    public Map<Evaluator<Annotation>, AnnotationConverter> defineConverters() {
        Map<Evaluator<Annotation>, AnnotationConverter> map = new HashMap<>();

        return map;
    }
}
