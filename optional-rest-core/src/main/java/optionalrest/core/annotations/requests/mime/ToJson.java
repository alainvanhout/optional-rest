package optionalrest.core.annotations.requests.mime;

import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.request.meta.Mime;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Handle(methods = {}, contentType = {}, accept = {Mime.APPLICATION_JSON})
public @interface ToJson {
}
