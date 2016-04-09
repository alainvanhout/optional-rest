package demo.renderers;

import demo.entities.Address;
import demo.entities.Person;
import context.Context;
import context.impl.ListContext;
import renderering.core.Renderer;
import renderering.core.manage.CachingRenderer;
import renderering.core.model.SimpleContextRenderer;
import renderering.core.model.SimpleModelRenderer;
import renderering.core.model.SimpleRendererContext;
import renderering.web.html.basic.documentbody.list.UnorderedListRenderer;

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
