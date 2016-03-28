package optionalrest.core.annotations.scopes;

import optionalrest.core.annotations.requests.RequestHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RequestHandler
public @interface Scope {

    /**
     * @return The id of the scope
     */
    String value() default "";
}
