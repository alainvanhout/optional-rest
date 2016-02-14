package alainvanhout.business.restservices;

import alainvanhout.business.Person;
import alainvanhout.business.repositories.PersonRepository;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.*;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RestEntityDefinition(name = "person", instanceClass = Person.class)
public class PersonRestService implements ScopeContainer {

    @Autowired
    @RestInstanceRelative(value = "address")
    private AddressRestService addressRestService;

    @Autowired
    private PersonRepository personRepository;

    @RestInstanceRelative(value = "pets")
    public RestResponse foo(RestRequest restRequest) {
        Person person = (Person) restRequest.getContext().get("person");
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person.getPets())));
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
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person)));
    }

    @RestEntity
    public void pass(RestRequest restRequest) {
        restRequest.getContext().put("persons", "passed");
    }

    @RestEntity
    public RestResponse arrive(RestRequest restRequest) {
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(personRepository.findAll())));
    }

    @RestRelative("women")
    public RestResponse women(RestRequest restRequest) {
        List<Person> women = personRepository.findAll().stream()
                .filter(p -> StringUtils.equals(p.getFirstName(), "Jane"))
                .collect(Collectors.toList());
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(women)));
    }

//    @RestEntity(methods = HttpMethod.OPTIONS)
//    public RestResponse entityOptions(RestRequest restRequest) {
//        String json = JsonUtils.entityToJson(Person.class);
//        return new RestResponse().renderer(new StringRenderer(json));
//    }

//    @RestError
//    public RestResponse error(RestRequest restRequest) {
//        RestException exception = restRequest.getFromContext("exception");
//        return new RestResponse().renderer(new StringRenderer("An error has occurred: " + exception.getMessage() + " "
//                + exception.getContext().entrySet().stream().map(e -> e.getKey() + ":" + JsonUtils.objectToJson(e.getValue())).collect(Collectors.joining(","))));
//    }

}
