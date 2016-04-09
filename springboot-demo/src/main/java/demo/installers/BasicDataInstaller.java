package demo.installers;

import demo.entities.Address;
import demo.entities.Person;
import demo.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;

@Component
public class BasicDataInstaller {

    @Autowired
    private PersonRepository personRepository;

    @PostConstruct
    private void setup(){
        install();
    }

    private void install() {
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
}
