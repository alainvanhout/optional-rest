package alainvanhout.cms.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import alainvanhout.sections.Section;

public interface SectionRepository extends MongoRepository<Section, String>{
}
