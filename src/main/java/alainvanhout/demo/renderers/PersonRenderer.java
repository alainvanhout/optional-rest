package alainvanhout.demo.renderers;

import alainvanhout.context.Context;
import alainvanhout.context.impl.ListContext;
import alainvanhout.context.impl.SimpleRendererContext;
import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;
import alainvanhout.renderering.renderer.html.basic.documentbody.list.UnorderedListRenderer;
import alainvanhout.renderering.renderer.manage.CachingRenderer;
import alainvanhout.renderering.renderer.model.SimpleModelRenderer;

public class PersonRenderer implements Renderer {

    private Renderer body;
    private Person person;
    private Renderer addressRenderer;
    private Context context;

    public PersonRenderer(Renderer body) {
        this.body = body;
    }

    public PersonRenderer set(Person person) {
        this.person = person;
        return this;
    }

    public PersonRenderer set(Context context) {
        this.context = context;
        return this;
    }

    public PersonRenderer set(SimpleModelRenderer<Address> addressRenderer) {
        this.addressRenderer = new CachingRenderer(addressRenderer);
        return this;
    }

    @Override
    public String render() {
        SimpleRendererContext personContext = new SimpleRendererContext();
        personContext.add("id", String.valueOf(person.getId()));
        personContext.add("firstName", person.getFirstName());
        personContext.add("lastName", person.getLastName());
        personContext.add("address", addressRenderer);
        personContext.add("pets", new UnorderedListRenderer().ignoreBlank(true).addItems(person.getPets()));

        return new SimpleContextRenderer(body, new ListContext(personContext, this.context)).render();
    }
}
