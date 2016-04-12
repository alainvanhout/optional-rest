package optionalrest.cms.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import renderering.core.retrieve.TextResourceRenderer;

import javax.annotation.PostConstruct;

@Component
public class TemplateDataInstaller {

    @Autowired
    private TemplateRepository templateRepository;

    @PostConstruct
    private void setup(){
        installTemplates();
    }

    private void installTemplates() {
        createTemplate("main", "templates/main.html", false);
        // templates
        createTemplate("template", "templates/templates/template.html", true);
        createTemplate("template-list", "templates/templates/template-list.html", true);
        createTemplate("template-edit", "templates/templates/template-edit.html", true);
        // persons
        createTemplate("person-large", "templates/persons/person-large.html", false);
        createTemplate("person-small", "templates/persons/person-small.html", false);
        createTemplate("address", "templates/persons/address.html", false);
    }

    private void createTemplate(String main, String resource, boolean always) {
        if (!templateRepository.exists(main) || always) {
            templateRepository.save(new Template(main, new TextResourceRenderer(resource).render()));
        }
    }
}
