package optionalrest.cms.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "templates")
public class Template {

    @Id
    private String name;
    private String body;

    public Template() {
    }

    public Template(String name) {
        this.name = name;
    }

    public Template(String name, String body) {
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
