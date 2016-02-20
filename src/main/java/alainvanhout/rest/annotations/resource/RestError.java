package alainvanhout.rest.annotations.resource;

import alainvanhout.rest.request.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RestError {
    String scope() default "";

    HttpMethod[] methods() default {HttpMethod.GET};
}
