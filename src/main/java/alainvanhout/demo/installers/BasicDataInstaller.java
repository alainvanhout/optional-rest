package alainvanhout.demo.installers;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.demo.Template;
import alainvanhout.demo.repositories.PersonRepository;
import alainvanhout.renderering.renderer.retrieve.TextResourceRenderer;
import alainvanhout.cms.repositories.SwitchRouteRepository;
import alainvanhout.cms.repositories.TemplateRepository;
import alainvanhout.cms.dtos.stored.StoredRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;

@Component
public class BasicDataInstaller {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private SwitchRouteRepository switchRouteRepository;

    @Autowired
    private PersonRepository personRepository;

    @PostConstruct
    private void setup(){
        install();
    }

    private void install() {
        installTemplates();
        installRoutes();
        installPersons();
    }

    private void installPersons() {
        if (personRepository.count() == 0){
            personRepository.save(createPerson("John", "Smith"));
            personRepository.save(createPerson("John", "Snow"));
            personRepository.save(createPerson("Jane", "Smith"));
            personRepository.save(createPerson("Jane", "Doe"));
            personRepository.save(createPerson("Jhon", "Doe"));
        }
    }

    private Person createPerson(String firstName, String lastName) {
        Person person = new Person();
        person.setId(BigInteger.valueOf(personRepository.count()));
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.getPets().addAll(Arrays.asList("Dog", "Cat", null));
        person.setAddress(new Address("Highstreet", "5", "54654", "London"));
        return person;
    }

    private void installTemplates() {
        createTemplate("main", "templates/main.html");
        createTemplate("person-large", "templates/person-large.html");
        createTemplate("person-small", "templates/person-small.html");
        createTemplate("address", "templates/address.html");
    }

    private void createTemplate(String main, String resource) {
        if (!templateRepository.exists(main)) {
            templateRepository.save(new Template(main, new TextResourceRenderer(resource).render()));
        }
    }

    private void installRoutes() {

        if (!switchRouteRepository.exists("root")) {
            StoredRoute personsRoute = new StoredRoute()
                    .templateId("persons");
            switchRouteRepository.save(personsRoute);

            StoredRoute root = new StoredRoute()
                    .id("root")
                    .templateId("main")
                    .addRoute("persons", personsRoute.getId());
            switchRouteRepository.save(root);
        }

    }
}
