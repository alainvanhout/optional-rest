package alainvanhout.demo.scopes;

import alainvanhout.cms.services.TemplateService;
import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.demo.renderers.PersonRenderer;
import alainvanhout.demo.repositories.PersonRepository;
import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.annotations.EntityDefinition;
import alainvanhout.optionalrest.annotations.ScopeDefinition;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.annotations.instance.RestInstance;
import alainvanhout.optionalrest.annotations.instance.RestInstanceRelative;
import alainvanhout.optionalrest.annotations.resource.RestRelative;
import alainvanhout.optionalrest.request.Headers;
import alainvanhout.optionalrest.request.Parameters;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.scope.ScopeContainer;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static alainvanhout.optionalrest.request.meta.Header.Accept.Text.HTML;

@Service
@ScopeDefinition(name = "person")
@EntityDefinition(instanceClass = Person.class)
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
    private void setup() {
        personRenderer = new PersonRenderer(templateService.findBodyAsRenderer("person-large"));
        adressRenderer = new SimpleModelRenderer<>(templateService.findBodyAsRenderer("address"));
        personRenderer.set(adressRenderer);
    }

    @RestInstanceRelative(path = "pets")
    public RestResponse foo(RestRequest restRequest) {
        Person person = (Person) restRequest.getContext().get("person");
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person.getPets())));
    }

    @RestInstance(methods = {HttpMethod.GET, HttpMethod.OPTIONS})
    public void id(RestRequest restRequest, HttpMethod method, Headers headers, Parameters parameters) {
        if (!HttpMethod.OPTIONS.equals(method)) {
            viewCount++;
            String id = restRequest.getPath().getStep();
            Person person = personRepository.findOne(BigInteger.valueOf(Long.valueOf(id)));
            restRequest.addToContext("person", person);
        }
    }

    @RestInstance
    public RestResponse idArrive(RestRequest restRequest) {
        Person person = restRequest.getFromContext("person");
        if (restRequest.getHeaders().contains("accept", HTML)) {
            personRenderer.set(person);
            adressRenderer.set(person.getAddress());
            return new RestResponse().renderer(personRenderer);
        }
        return new RestResponse().renderer(new PreRenderer(JsonUtils.objectToJson(person)));
    }

    @RestRelative(path = "views")
    private RestResponse viewCount(RestRequest restRequest) {
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
}
