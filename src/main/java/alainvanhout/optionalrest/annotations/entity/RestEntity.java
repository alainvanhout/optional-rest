package alainvanhout.optionalrest.annotations.entity;

import alainvanhout.optionalrest.request.meta.Mime;
import alainvanhout.optionalrest.request.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RestEntity {
    String scope() default "";

    HttpMethod[] methods() default {HttpMethod.GET};

    String[] accepts() default {Mime.TEXT_HTML, Mime.APPLICATION_JSON};
}
