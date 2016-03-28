package alainvanhout.demo.sections;

import alainvanhout.demo.entities.Person;
import alainvanhout.demo.renderers.PersonRenderer;
import alainvanhout.demo.repositories.PersonRepository;
import alainvanhout.demo.services.RendererService;
import renderering.core.Renderer;
import renderering.web.html.basic.documentbody.list.UnorderedListRenderer;
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
