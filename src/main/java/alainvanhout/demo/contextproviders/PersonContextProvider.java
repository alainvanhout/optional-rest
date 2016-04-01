package alainvanhout.demo.contextproviders;

import alainvanhout.demo.entities.Person;
import alainvanhout.demo.repositories.PersonRepository;
import context.provider.ContextProvider;
import alainvanhout.routing.path.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class PersonContextProvider implements ContextProvider{

    @Autowired
    private PersonRepository personRepository;

    @Override
    public String getId() {
        return "myPerson";
    }

    @Override
    public void handle(Path path) {
        String step = path.getStep();
        Person person = personRepository.findOne(BigInteger.valueOf(Long.valueOf(step)));
        path.addToContext("myPersonId", step);
    }
}
