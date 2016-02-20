package alainvanhout.business.scopes;

import alainvanhout.business.entities.Address;
import alainvanhout.business.entities.Person;
import alainvanhout.business.renderers.PersonRenderer;
import alainvanhout.business.repositories.PersonRepository;
import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.cms.services.TemplateService;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;
import alainvanhout.rest.RestResponse;
import alainvanhout.rest.annotations.*;
import alainvanhout.rest.annotations.entity.RestEntity;
import alainvanhout.rest.annotations.instance.RestInstance;
import alainvanhout.rest.annotations.instance.RestInstanceRelative;
import alainvanhout.rest.annotations.resource.RestRelative;
import alainvanhout.rest.request.RestRequest;
import alainvanhout.rest.request.meta.Header;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static alainvanhout.rest.request.meta.Header.Accept.Text.HTML;

@Service
@RestEntityDefinition(entityName = "person", instanceClass = Person.class)
public class PersonScope implements ScopeContainer {

    @RestInstanceRelative(path = "address")
    private AddressScope addressScope;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TemplateService templateService;

    private PersonRenderer personRenderer;
    private SimpleModelRenderer<Address> adressRenderer;

    private int viewCount;

    @PostConstruct
    private void setup(){
        personRenderer = new PersonRenderer(templateService.findBodyAsRenderer("person-large"));
        adressRenderer = new SimpleModelRenderer<>(templateService.findBodyAsRenderer("address"));
        personRenderer.set(adressRenderer);
    }

    @RestInstanceRelative(path = "pets")
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
        viewCount++;
        Person person = restRequest.getFromContext("person");
        if (restRequest.getHeaders().contains("accept", HTML)) {
            personRenderer.set(person);
            adressRenderer.set(person.getAddress());
            return new RestResponse().renderer(personRenderer);
        }
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person)));
    }

    @RestRelative(path = "views")
    private RestResponse viewCount(RestRequest restRequest){
        return new RestResponse().renderer(new StringRenderer("View count:" + viewCount));
    }

    @RestEntity
    public RestResponse arrive(RestRequest restRequest) {
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(personRepository.findAll())));
    }

    @RestRelative(path = "women")
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
