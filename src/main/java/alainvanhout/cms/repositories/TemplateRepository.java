package alainvanhout.cms.repositories;

import alainvanhout.demo.Template;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateRepository extends MongoRepository<Template, String>{
    Template findByName(String name);
}
