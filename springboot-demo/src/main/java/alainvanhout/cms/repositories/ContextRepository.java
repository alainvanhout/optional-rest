package alainvanhout.cms.repositories;

import alainvanhout.cms.dtos.stored.StoredContext;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextRepository extends MongoRepository<StoredContext, String>{
}
