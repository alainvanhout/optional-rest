package alainvanhout.optionalrest.annotations;

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
