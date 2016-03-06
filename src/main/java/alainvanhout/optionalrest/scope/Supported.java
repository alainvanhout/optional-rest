package alainvanhout.optionalrest.scope;

import alainvanhout.optionalrest.request.meta.HttpMethod;
import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.util.*;

public class Supported {
    private Set<HttpMethod> methods =  new HashSet<>();
    private Set<String> accepts = new HashSet<>();

    public Set<HttpMethod> getMethods() {
        return methods;
    }

    public Supported methods (Collection<HttpMethod> methods){
        this.methods.addAll(methods);
        return  this;
    }


    public Supported methods (HttpMethod[] methods){
        return methods(Arrays.asList(methods));
    }

    public Set<String> getAccepts() {
        return this.accepts;
    }

    public Supported accepts(Collection<String> accepts) {
        this.accepts.addAll(accepts);
        return this;
    }

    public Supported accepts(String[] accepts) {
        return accepts(Arrays.asList(accepts));
    }
}
