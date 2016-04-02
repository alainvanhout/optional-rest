package optionalrest.core.annotations.requests;

import optionalrest.core.request.meta.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Allows setting all Mapping properties related to content negotiation in the broadest sense
 */
@Retention(RetentionPolicy.RUNTIME)
@RequestHandler
public @interface Handle {

    HttpMethod[] methods() default {};

    String[] accept() default {};

    String[] contentType() default {};
}
