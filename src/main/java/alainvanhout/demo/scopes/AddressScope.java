package alainvanhout.demo.scopes;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.response.RendererResponse;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.annotations.resource.RestError;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import org.springframework.stereotype.Service;

@Service
@ScopeDefinition(name = "address")
@EntityDefinition(instanceClass = Address.class)
public class AddressScope implements ScopeContainer {

    @RestEntity
    public Response arrive(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new RendererResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person.getAddress())));
    }

    @RestEntity
    public Renderer arriveInstance(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new PreRenderer(JsonUtils.objectToJson(person.getAddress()));
    }

    @RestError
    public String error(RestException exception) {
        return "An address error has occurred > " + exception.getMessage();
    }
}
