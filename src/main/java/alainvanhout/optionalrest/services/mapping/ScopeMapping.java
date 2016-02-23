package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.Scope;

import java.util.function.Supplier;

public class ScopeMapping implements Mapping {

    private Supplier<Scope> supplier;

    public ScopeMapping(Supplier<Scope> supplier) {
        this.supplier = supplier;
    }

    @Override
    public RestResponse call(RestRequest restRequest) {
        Scope scope = supplier.get();
        return scope.follow(restRequest);
    }
}
