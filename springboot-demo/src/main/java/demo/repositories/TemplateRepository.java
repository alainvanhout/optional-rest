package demo.repositories;

import demo.entities.Template;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateRepository extends MongoRepository<Template, String>{
    Template findByName(String name);
}
