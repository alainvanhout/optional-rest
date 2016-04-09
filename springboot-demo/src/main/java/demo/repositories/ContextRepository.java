package demo.repositories;

import demo.dtos.StoredContext;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContextRepository extends MongoRepository<StoredContext, String>{
}
