package alainvanhout.business.renderers;

import alainvanhout.business.entities.Address;
import alainvanhout.business.entities.Person;
import alainvanhout.context.impl.SimpleRendererContext;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;
import alainvanhout.renderering.renderer.manage.CachingRenderer;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.list.UnorderedListRenderer;

public class PersonRenderer implements Renderer {

    private Renderer body;
    private Person person;
    private Renderer addressRenderer;

    public PersonRenderer(Renderer body) {
        this.body = body;
    }

    public PersonRenderer set(Person person) {
        this.person = person;
        return this;
    }

    public PersonRenderer set(SimpleModelRenderer<Address> addressRenderer) {
        this.addressRenderer = new CachingRenderer(addressRenderer);
        return this;
    }

    @Override
    public String render() {
        SimpleRendererContext context = new SimpleRendererContext();
        context.add("id", String.valueOf(person.getId()));
        context.add("firstName", person.getFirstName());
        context.add("lastName", person.getLastName());
        context.add("address", addressRenderer);
//        context.add("address", new SimpleModelRenderer<Address>(new TextResourceRenderer("templates/address.html")).set(person.getAddress()));
        context.add("pets", new UnorderedListRenderer().ignoreBlank(true).addItems(person.getPets()));
        return  new SimpleContextRenderer(body, context).render();
    }
}
