package alainvanhout.optionalrest.annotations.resource;

import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.request.meta.Mime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RestScope {
    String scope() default "";

    HttpMethod[] methods() default {HttpMethod.GET};

    String[] accept() default {Mime.TEXT_HTML, Mime.APPLICATION_JSON};

    String[] contentType() default {};
}
