package optionalrest.spring.converters;

import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.services.mapping.providers.Evaluator;
import optionalrest.core.services.mapping.providers.annotations.AnnotationConverter;
import optionalrest.core.services.mapping.providers.annotations.AnnotationConverterProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.util.*;

@Component
public class SpringAnnotationConverterProvider implements AnnotationConverterProvider {

    @Override
    public Map<Class, AnnotationConverter> defineConvertersForClass() {
        Map<Class, AnnotationConverter> map = new HashMap<>();

        map.put(RequestMapping.class, p -> {
            Handle o = new Handle() {
                RequestMapping r = (RequestMapping) p;

                @Override
                public HttpMethod[] methods() {
                    return Arrays.asList(r.method()).stream()
                            .map(r -> HttpMethod.valueOf(r.name()))
                            .toArray(HttpMethod[]::new);
                }

                @Override
                public String[] accept() {
                    return r.produces();
                }

                @Override
                public String[] contentType() {
                    return r.consumes();
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return Handle.class;
                }
            };

            Annotation marker = new optionalrest.core.annotations.requests.RequestHandler() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return optionalrest.core.annotations.requests.RequestHandler.class;
                }
            };

            Collection<Annotation> singleton = Arrays.asList(o, marker);
            return singleton;
        });

        return map;
    }

    @Override
    public Map<Evaluator<Annotation>, AnnotationConverter> defineConverters() {
        Map<Evaluator<Annotation>, AnnotationConverter> map = new HashMap<>();

        return map;
    }
}
