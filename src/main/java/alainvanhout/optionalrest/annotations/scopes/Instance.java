package alainvanhout.optionalrest.annotations.scopes;

import alainvanhout.optionalrest.annotations.requests.Handle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Handle
public @interface Instance {
}
