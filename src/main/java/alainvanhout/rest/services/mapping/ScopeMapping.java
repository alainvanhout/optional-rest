package alainvanhout.rest.services.mapping;

import alainvanhout.rest.RestResponse;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.Scope;

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
