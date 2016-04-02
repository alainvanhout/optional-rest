package optionalrest.core.annotations.requests.mime;

import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.request.meta.Mime;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Handle(methods = {}, contentType = {}, accept = {Mime.TEXT_XML, Mime.APPLICATION_XML})
public @interface ToXml {
}
