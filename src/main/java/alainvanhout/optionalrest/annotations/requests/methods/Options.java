package alainvanhout.optionalrest.annotations.requests.methods;

import alainvanhout.optionalrest.annotations.requests.Handle;
import alainvanhout.optionalrest.request.meta.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Handle(methods = HttpMethod.OPTIONS, contentType = {}, accept = {})
public @interface Options {
}
