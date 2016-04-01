package optionalrest.spring.annotations;

import optionalrest.spring.OptionalRestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Import(OptionalRestConfiguration.class)

public @interface EnableOptionalRest {
}
