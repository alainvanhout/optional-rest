package alainvanhout.demo.scopes;

import alainvanhout.cms.services.TemplateService;
import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.demo.renderers.PersonRenderer;
import alainvanhout.demo.repositories.PersonRepository;
import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.*;
import alainvanhout.optionalrest.annotations.Error;
import alainvanhout.optionalrest.request.Headers;
import alainvanhout.optionalrest.request.Parameters;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.request.meta.Mime;
import alainvanhout.optionalrest.response.FileResponse;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.factories.FromContext;
import alainvanhout.optionalrest.utils.JsonUtils;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.PreRenderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ScopeDefinition(name = "person")
@Entity(Person.class)
@Description("People, including address information")
public class PersonScope implements ScopeContainer {

    @Instance
    @Relative(path = "address")
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

    @Instance
    @Relative(path = "pets")
    public Renderer foo(Request request, @FromContext("person") Person person) {
        return new PreRenderer(JsonUtils.objectToJson(person.getPets()));
    }

    @Instance
    @Handle(methods = {HttpMethod.GET, HttpMethod.OPTIONS})
    public void id(Request request, HttpMethod method, Headers headers, Parameters parameters) {
        if (!HttpMethod.OPTIONS.equals(method)) {
            viewCount++;
            String id = request.getPath().getStep();
            Person person = personRepository.findOne(BigInteger.valueOf(Long.valueOf(id)));
            request.getContext().add("person", person);
        }
    }

    @Instance
    public Renderer idArrive(Request request) {
        Person person = request.getContext().get("person");
        if (request.getHeaders().contains("accept", Mime.TEXT_HTML)) {
            personRenderer.set(person);
            adressRenderer.set(person.getAddress());
            return personRenderer;
        }
        return new PreRenderer(JsonUtils.objectToJson(person));
    }

    @Relative(path = "views")
    private String viewCount(Request request) {
        return "View count:" + viewCount;
    }

    @Handle
    public Renderer arrive(Request request) {
        return new PreRenderer(JsonUtils.objectToJson(personRepository.findAll()));
    }

    @Relative(path = "women")
    public Renderer women(Request request) {
        List<Person> women = personRepository.findAll().stream()
                .filter(p -> StringUtils.equals(p.getFirstName(), "Jane"))
                .collect(Collectors.toList());
        return new PreRenderer(JsonUtils.objectToJson(women));
    }

    @Instance
    @Relative(path = "image")
    public Response image() {
        return new FileResponse().resource("/images/image.jpg");
    }

    @Error
    public String error(RestException exception) {
        return "An Person error has occurred > " + exception.getMessage();
    }
}
