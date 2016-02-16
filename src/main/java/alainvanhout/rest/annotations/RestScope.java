package alainvanhout.rest.annotations;

import alainvanhout.rest.request.meta.Header;
import alainvanhout.rest.request.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static alainvanhout.rest.request.meta.Header.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface RestScope {
    HttpMethod[] methods() default {HttpMethod.GET};
    String[] accepts() default {Accept.HTML, Accept.JSON};
}
