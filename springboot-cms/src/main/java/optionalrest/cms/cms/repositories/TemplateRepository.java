package optionalrest.cms.cms.repositories;

import optionalrest.cms.entities.Template;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateRepository extends MongoRepository<Template, String>{
    Template findByName(String name);
}
