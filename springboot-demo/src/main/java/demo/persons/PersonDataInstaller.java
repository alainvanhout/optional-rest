package demo.persons;

import demo.addresses.Address;
import demo.addresses.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PersonDataInstaller {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

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
        Address address = new Address();
        address.setCity("London");
        address.setNumber("5");
        address.setStreet("Highstreet");
        address.setPostalCode("55654");
        addressRepository.save(address);

        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);

        person.setAddress(address);
        return person;
    }
}
