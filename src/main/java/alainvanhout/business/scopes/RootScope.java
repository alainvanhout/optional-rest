package alainvanhout.business.scopes;

import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.RestEntity;
import alainvanhout.rest.annotations.RestRelative;
import alainvanhout.rest.request.meta.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.ScopeContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RootScope implements ScopeContainer{

    @Autowired
    @RestRelative("persons")
    private PersonScope personRestService;

    @Autowired
    @RestRelative("addresses")
    private AddressScope addressRestService;

    @RestEntity
    private RestResponse arrive(RestRequest restRequest){
        return new RestResponse().renderer(new StringRenderer("Root"));
    }

    @RestEntity
    private void pass(RestRequest restRequest){
        if (restRequest.getParameters().contains("OPTIONS")){
            restRequest.method(HttpMethod.OPTIONS);
        }
    }


}
