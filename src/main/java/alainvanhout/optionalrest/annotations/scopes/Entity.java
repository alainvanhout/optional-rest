package alainvanhout.optionalrest.annotations.scopes;

import alainvanhout.optionalrest.annotations.requests.RequestHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RequestHandler
public @interface Entity {
    Class value();
}
