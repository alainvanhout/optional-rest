package alainvanhout.business.sections;

import alainvanhout.business.Address;
import alainvanhout.business.Person;
import alainvanhout.business.repositories.PersonRepository;
import alainvanhout.business.services.RendererService;
import alainvanhout.context.Context;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;
import alainvanhout.routing.path.Path;
import alainvanhout.cms.dtos.custom.CustomSection;
import alainvanhout.cms.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class AddressSection implements CustomSection {

    @Autowired
    private RendererService rendererService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TemplateService templateService;

    @Override
    public String getId() {
        return "address";
    }

    @Override
    public Renderer getRenderer(Path path) {
        Person person = retrievePerson(path.getContext());
        return new SimpleModelRenderer<Address>(retrieveTemplate(path.getContext())).set(person.getAddress());
    }

    private Person retrievePerson(Context context) {
        String personId = context.getAs("personId");
        return personRepository.findOne(BigInteger.valueOf(Long.valueOf(personId)));
    }

    private Renderer retrieveTemplate(Context context) {
        return templateService.findBodyAsRenderer("address");
    }
}
