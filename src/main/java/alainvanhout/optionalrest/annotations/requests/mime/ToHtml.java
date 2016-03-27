package alainvanhout.optionalrest.annotations.requests.mime;

import alainvanhout.optionalrest.annotations.requests.Handle;
import alainvanhout.optionalrest.request.meta.Mime;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Handle(methods = {}, contentType = {}, accept = {Mime.TEXT_HTML})
public @interface ToHtml {
}
