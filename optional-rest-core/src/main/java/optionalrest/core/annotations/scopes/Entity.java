package optionalrest.core.annotations.scopes;

import optionalrest.core.annotations.requests.RequestHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RequestHandler
public @interface Entity {
    Class value();
}
