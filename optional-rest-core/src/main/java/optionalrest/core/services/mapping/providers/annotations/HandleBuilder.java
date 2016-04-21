package optionalrest.core.services.mapping.providers.annotations;

import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.request.meta.HttpMethod;

import java.lang.annotation.Annotation;
import java.util.List;

public class HandleBuilder {

    public static final Annotation MARKER = new optionalrest.core.annotations.requests.RequestHandler() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return optionalrest.core.annotations.requests.RequestHandler.class;
        }
    };

    private HttpMethod[] methods;
    private String[] accept;
    private String[] contentType;

    public Handle build(){
        return new Handle() {
            @Override
            public HttpMethod[] methods() {
                return methods;
            }

            @Override
            public String[] accept() {
                return accept;
            }

            @Override
            public String[] contentType() {
                return contentType;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Handle.class;
            }
        };
    }

    public HttpMethod[] getMethods() {
        return this.methods;
    }

    public HandleBuilder methods(HttpMethod[] methods) {
        this.methods = methods;
        return this;
    }


    public HandleBuilder methods(List<HttpMethod> methods) {
        this.methods = methods.toArray(new HttpMethod[]{});
        return this;
    }

    public String[] getAccept() {
        return this.accept;
    }

    public HandleBuilder accept(String[] accept) {
        this.accept = accept;
        return this;
    }

    public String[] getContentType() {
        return this.contentType;
    }

    public HandleBuilder contentType(String[] contentType) {
        this.contentType = contentType;
        return this;
    }
}
