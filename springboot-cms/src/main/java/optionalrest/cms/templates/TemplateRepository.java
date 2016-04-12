package optionalrest.cms.templates;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateRepository extends MongoRepository<Template, String>{
    Template findByName(String name);
}
