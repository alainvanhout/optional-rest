package alainvanhout.optionalrest.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RequestHandler
public @interface Entity {
    Class value();
}
