package alainvanhout.business.sections;

import alainvanhout.business.entities.Person;
import alainvanhout.business.renderers.PersonRenderer;
import alainvanhout.business.repositories.PersonRepository;
import alainvanhout.business.services.RendererService;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.list.UnorderedListRenderer;
import alainvanhout.routing.path.Path;
import alainvanhout.cms.dtos.custom.CustomSection;
import alainvanhout.cms.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PersonsSection implements CustomSection {

    @Autowired
    private RendererService rendererService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TemplateService templateService;

    @Override
    public String getId() {
        return "persons";
    }

    @Override
    public Renderer getRenderer(Path path) {
        UnorderedListRenderer list = new UnorderedListRenderer();
        Collection<Person> people = personRepository.findAll();
        for (Person person : people) {
            list.add(new PersonRenderer(templateService.findBodyAsRenderer("person-small")).set(person));
        }
        return list;
  }
}
