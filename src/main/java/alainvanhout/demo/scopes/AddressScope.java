package alainvanhout.demo.scopes;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.resource.RestError;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.utils.JsonUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@ScopeDefinition(name = "address")
@EntityDefinition(instanceClass = Address.class)
public class AddressScope implements ScopeContainer {

    // TODO: this should be RestInstance + Address should have its own repository
    @RestEntity
    public RestResponse arrive(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person.getAddress())));
    }
    @RestEntity
    public RestResponse arriveInstance(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person.getAddress())));
    }

    @RestError
    public RestResponse error(RestRequest restRequest) {
        return new RestResponse().renderer(new PreRenderer("An address error has occurred"));
    }
}
