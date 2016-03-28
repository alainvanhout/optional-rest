package optionalrest.core.scope;

import optionalrest.core.request.meta.HttpMethod;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Supported {
    private Set<HttpMethod> methods = new HashSet<>();
    private Set<String> accept = new HashSet<>();
    private Set<String> contentType = new HashSet<>();

    public Set<HttpMethod> getMethods() {
        return methods;
    }

    public Supported methods(Collection<HttpMethod> methods) {
        this.methods.addAll(methods);
        return this;
    }

    public Supported methods(HttpMethod[] methods) {
        return methods(Arrays.asList(methods));
    }

    public Set<String> getAccept() {
        return this.accept;
    }

    public Supported accept(Collection<String> accepts) {
        this.accept.addAll(accepts);
        return this;
    }

    public Supported accept(String[] accepts) {
        return accept(Arrays.asList(accepts));
    }

    public Set<String> getContentType() {
        return this.contentType;
    }

    public Supported contentType(Collection<String> contentType) {
        this.contentType.addAll(contentType);
        return this;
    }

    public Supported contentType(String[] contentType) {
        return contentType(Arrays.asList(contentType));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
