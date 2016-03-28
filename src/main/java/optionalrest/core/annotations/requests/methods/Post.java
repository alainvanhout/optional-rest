package optionalrest.core.annotations.requests.methods;

import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.request.meta.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Handle(methods = HttpMethod.POST, contentType = {}, accept = {})
public @interface Post {
}
