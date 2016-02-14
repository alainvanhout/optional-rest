package alainvanhout.business.restservices;

import alainvanhout.business.Address;
import alainvanhout.business.Person;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.RestEntity;
import alainvanhout.rest.annotations.RestEntityDefinition;
import alainvanhout.rest.annotations.RestError;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.utils.JsonUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RestEntityDefinition(name = "address", instanceClass = Address.class)
public class AddressRestService implements ScopeContainer {

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
