package optionalrest.cms.contexts;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextRepository extends MongoRepository<StoredContext, String>{
}
