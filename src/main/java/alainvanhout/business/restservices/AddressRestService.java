package alainvanhout.business.restservices;

import alainvanhout.business.Person;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.RestEntity;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.restservice.RestService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Service;

@Service
public class AddressRestService extends RestService {
    @RestEntity
    public RestResponse arrive(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new RestResponse().renderer(new StringRenderer(ToStringBuilder.reflectionToString(person.getAddress(), ToStringStyle.JSON_STYLE)));
    }
}
