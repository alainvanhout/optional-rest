package alainvanhout.demo.scopes;

import alainvanhout.optionalrest.response.Response;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.optionalrest.response.RendererResponse;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.annotations.resource.RestRelative;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;
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
    private Response arrive(RestRequest restRequest){
        return new RendererResponse().renderer(new StringRenderer("Root"));
    }

    @RestEntity
    private void pass(RestRequest restRequest){
        if (restRequest.getParameters().contains(OPTIONS)){
            restRequest.method(HttpMethod.OPTIONS);
        }
    }
}
