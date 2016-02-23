package alainvanhout.optionalrest.annotations.instance;

import alainvanhout.optionalrest.request.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RestInstanceRelative {
    String path() default "";

    String parentScope() default "";

    String instanceScope() default "";

    String relativeScope() default "";

    HttpMethod[] methods() default {HttpMethod.GET};
}
