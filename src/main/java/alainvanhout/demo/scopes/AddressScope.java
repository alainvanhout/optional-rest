package alainvanhout.demo.scopes;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.resource.RestError;
import alainvanhout.optionalrest.annotations.resource.RestScope;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import org.springframework.stereotype.Service;

@Service
@ScopeDefinition(name = "address")
@EntityDefinition(instanceClass = Address.class)
public class AddressScope implements ScopeContainer {

    @RestScope
    public Renderer arrive(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new PreRenderer(JsonUtils.objectToJson(person.getAddress()));
    }

    @RestError
    public String error(RestException exception) {
        return "An address error has occurred > " + exception.getMessage();
    }
}
