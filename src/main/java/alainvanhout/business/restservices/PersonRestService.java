package alainvanhout.business.restservices;

import alainvanhout.business.Person;
import alainvanhout.business.repositories.PersonRepository;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestException;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.*;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.services.RestService;
import alainvanhout.rest.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.stream.Collectors;

@Service
public class PersonRestService extends RestService {

    @Autowired
    @RestInstanceRelative(value = "address")
    private AddressRestService addressRestService;

    @Autowired
    private PersonRepository personRepository;

    @RestEntityDefinition
    private Class person = Person.class;

    @RestInstanceRelative(value = "pets")
    public RestResponse foo(RestRequest restRequest) {
        Person person = (Person) restRequest.getContext().get("person");
        return new RestResponse().renderer(new StringRenderer(ToStringBuilder.reflectionToString(person.getPets().toArray(), ToStringStyle.JSON_STYLE)));
    }

    @RestInstance
    public void id(RestRequest restRequest) {
        String id = restRequest.getPath().getStep();
        Person person = personRepository.findOne(BigInteger.valueOf(Long.valueOf(id)));
        restRequest.addToContext("person", person);
    }

    @RestInstance
    public RestResponse idArrive(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        return new RestResponse().renderer(new StringRenderer(ToStringBuilder.reflectionToString(person, ToStringStyle.JSON_STYLE)));
    }

    @RestEntity
    public void pass(RestRequest restRequest) {
        restRequest.getContext().put("persons", "passed");
    }

    @RestEntity
    public RestResponse arrive(RestRequest restRequest) {
        String content = personRepository.findAll().stream()
                .map(p -> ToStringBuilder.reflectionToString(p, ToStringStyle.JSON_STYLE))
                .collect(Collectors.joining(","));
        return new RestResponse().renderer(new StringRenderer("[" + content + "]"));
    }

    @RestRelative("women")
    public RestResponse women(RestRequest restRequest) {
        String content = personRepository.findAll().stream()
                .filter(p -> StringUtils.equals(p.getFirstName(), "Jane"))
                .map(p -> ToStringBuilder.reflectionToString(p, ToStringStyle.JSON_STYLE))
                .collect(Collectors.joining(","));
        return new RestResponse().renderer(new StringRenderer("[" + content + "]"));
    }

    @RestEntity(methods = HttpMethod.OPTIONS)
    public RestResponse entityOptions(RestRequest restRequest) {
        String json = JsonUtils.entityToJson(Person.class);
        return new RestResponse().renderer(new StringRenderer(json));
    }

    @RestError
    public RestResponse error(RestRequest restRequest) {
        RestException exception = restRequest.getFromContext("exception");
        return new RestResponse().renderer(new StringRenderer("An error has occurred: " + exception.getMessage() + " "
                + exception.getContext().entrySet().stream().map(e -> e.getKey() + ":" + ToStringBuilder.reflectionToString(e.getValue(), ToStringStyle.JSON_STYLE)).collect(Collectors.joining(","))));
    }

}
