package demo.installers;

import demo.entities.Address;
import demo.entities.Person;
import demo.entities.Template;
import demo.repositories.PersonRepository;
import renderering.core.retrieve.TextResourceRenderer;
import demo.repositories.TemplateRepository;
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
    private PersonRepository personRepository;

    @PostConstruct
    private void setup(){
        install();
    }

    private void install() {
        installTemplates();
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
        createTemplate("main", "templates/main.html", false);
        // templates
        createTemplate("template", "templates/templates/template.html", true);
        createTemplate("template-list", "templates/templates/template-list.html", true);
        createTemplate("template-edit", "templates/templates/template-edit.html", true);
        // persons
        createTemplate("person-large", "templates/persons/person-large.html", false);
        createTemplate("person-small", "templates/persons/person-small.html", false);
        createTemplate("address", "templates/persons/address.html", false);
    }

    private void createTemplate(String main, String resource, boolean always) {
        if (!templateRepository.exists(main) || always) {
            templateRepository.save(new Template(main, new TextResourceRenderer(resource).render()));
        }
    }
}
