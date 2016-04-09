package optionalrest.cms.cms.repositories;

import optionalrest.cms.cms.dtos.stored.StoredContext;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextRepository extends MongoRepository<StoredContext, String>{
}
