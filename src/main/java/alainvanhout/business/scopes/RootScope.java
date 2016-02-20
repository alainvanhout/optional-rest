package alainvanhout.business.scopes;

import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.entity.RestEntity;
import alainvanhout.rest.annotations.resource.RestRelative;
import alainvanhout.rest.request.meta.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.ScopeContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RootScope implements ScopeContainer{

    public static final String OPTIONS = "OPTIONS";

    @RestRelative(path = "persons")
    private PersonScope personScope;

    @RestRelative(path = "person")
    private PersonScope personScopeAlternate;

    @Autowired
    @RestRelative(path = "addresses")
    private AddressScope addressScope;

    @RestEntity
    private RestResponse arrive(RestRequest restRequest){
        return new RestResponse().renderer(new StringRenderer("Root"));
    }

    @RestEntity
    private void pass(RestRequest restRequest){
        if (restRequest.getParameters().contains(OPTIONS)){
            restRequest.method(HttpMethod.OPTIONS);
        }
    }


}
