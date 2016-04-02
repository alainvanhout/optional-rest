package alainvanhout.demo.repositories;

import alainvanhout.demo.entities.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PersonRepository extends MongoRepository<Person, BigInteger> {
}
