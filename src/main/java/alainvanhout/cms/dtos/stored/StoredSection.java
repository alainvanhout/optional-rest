package alainvanhout.cms.dtos.stored;

import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.routing.path.Path;
import alainvanhout.sections.Section;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sections")
public class StoredSection implements Section {

    @Id
    private String id;
    private String TemplateId;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Renderer getRenderer(Path context) {
        throw new UnsupportedOperationException();
    }

    public String getTemplateId() {
        return TemplateId;
    }

    public void setTemplateId(String templateId) {
        TemplateId = templateId;
    }
}
