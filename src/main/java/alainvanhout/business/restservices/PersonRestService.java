package alainvanhout.business.restservices;

import alainvanhout.business.Person;
import alainvanhout.business.repositories.PersonRepository;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.utils.JsonUtils;
import alainvanhout.rest.annotations.*;
import alainvanhout.rest.request.HttpMethod;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.restservice.RestService;
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
    @RestRelative(value = "address", forInstance = true)
    private AddressRestService addressRestService;

    @Autowired
    private PersonRepository personRepository;

    @RestRelative(value = "pets", forInstance = true)
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

//    @RestEntity(methods = HttpMethod.OPTIONS)
//    public RestResponse entityOpions(RestRequest restRequest){
//        String json = RestUtils.entityToJson(Person.class);
//        return new RestResponse().renderer(new StringRenderer(json));
//    }


    @Override
    public Class getEntityClass() {
        return Person.class;
    }

    @RestInstance(methods = HttpMethod.OPTIONS)
    public RestResponse instanceOptions(RestRequest restRequest){
        String json = JsonUtils.entityToJson(Person.class);
        return new RestResponse().renderer(new StringRenderer(json));
    }
}
