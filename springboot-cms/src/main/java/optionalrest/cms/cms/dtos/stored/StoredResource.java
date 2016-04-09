package optionalrest.cms.cms.dtos.stored;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resources")
public class StoredResource {

    @Id
    private String id;
    private String name;
    private String type;
    private String body;

}
