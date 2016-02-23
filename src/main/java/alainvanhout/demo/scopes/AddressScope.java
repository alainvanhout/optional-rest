package alainvanhout.demo.scopes;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.entity.RestEntity;
import alainvanhout.rest.annotations.RestEntityDefinition;
import alainvanhout.rest.annotations.resource.RestError;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Service
@RestEntityDefinition(entityName = "address", instanceClass = Address.class)
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
