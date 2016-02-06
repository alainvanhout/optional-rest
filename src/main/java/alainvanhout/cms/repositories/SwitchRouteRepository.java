package alainvanhout.cms.repositories;

import alainvanhout.cms.dtos.stored.StoredRoute;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SwitchRouteRepository extends MongoRepository<StoredRoute, String> {
}
