package alainvanhout.optionalrest.annotations;

import alainvanhout.optionalrest.request.meta.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RequestHandler
public @interface Handle {

    HttpMethod[] methods() default {};

    String[] accept() default {};

    String[] contentType() default {};
}
