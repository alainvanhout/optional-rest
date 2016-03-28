package alainvanhout.demo.sections;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.demo.renderers.PersonRenderer;
import alainvanhout.demo.repositories.PersonRepository;
import alainvanhout.demo.services.RendererService;
import alainvanhout.context.Context;
import renderering.core.Renderer;
import renderering.core.model.SimpleModelRenderer;
import alainvanhout.routing.path.Path;
import alainvanhout.cms.dtos.custom.CustomSection;
import alainvanhout.cms.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class PersonSection implements CustomSection {

    @Autowired
    private RendererService rendererService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TemplateService templateService;

    @Override
    public String getId() {
        return "person";
    }

    @Override
    public Renderer getRenderer(Path path) {
        Person person = retrievePerson(path.getContext());
        return new PersonRenderer(retrieveTemplate(path.getContext()))
                .set(person)
                .set(new SimpleModelRenderer<Address>(templateService.findBodyAsRenderer("address")).set(person.getAddress()));
    }

    private Person retrievePerson(Context context) {
        String personId = context.get("personId");
        return personRepository.findOne(BigInteger.valueOf(Long.valueOf(personId)));
    }

    private Renderer retrieveTemplate(Context context) {
        return templateService.findBodyAsRenderer("person-large");
    }
}
