package alainvanhout.rest.annotations.entity;

import alainvanhout.rest.request.meta.Header;
import alainvanhout.rest.request.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RestEntity {
    String scope() default "";

    HttpMethod[] methods() default {HttpMethod.GET};

    String[] accepts() default {Header.Accept.Text.HTML, Header.Accept.Application.JSON};
}
