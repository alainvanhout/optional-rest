package alainvanhout.business.restservices;

import alainvanhout.business.Address;
import alainvanhout.business.Person;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.RestEntity;
import alainvanhout.rest.annotations.RestEntityDefinition;
import alainvanhout.rest.annotations.RestError;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.services.RestService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Service;

@Service
public class AddressRestService extends RestService {

    @RestEntityDefinition
    private Address address;

    @RestEntity
    public RestResponse arrive(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new RestResponse().renderer(new StringRenderer(ToStringBuilder.reflectionToString(person.getAddress(), ToStringStyle.JSON_STYLE)));
    }

    @RestError
    public RestResponse error(RestRequest restRequest) {
        return new RestResponse().renderer(new StringRenderer("An address error has occurred"));
    }
}
