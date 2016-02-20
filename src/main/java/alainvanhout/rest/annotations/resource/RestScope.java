package alainvanhout.rest.annotations.resource;

import alainvanhout.rest.request.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static alainvanhout.rest.request.meta.Header.Accept;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RestScope {
    String scope() default "";

    HttpMethod[] methods() default {HttpMethod.GET};

    String[] accepts() default {Accept.Text.HTML, Accept.Application.JSON};
}
