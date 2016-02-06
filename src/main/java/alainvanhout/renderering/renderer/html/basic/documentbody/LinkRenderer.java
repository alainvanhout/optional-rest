package alainvanhout.renderering.renderer.html.basic.documentbody;

import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class LinkRenderer extends GenericElementRenderer {

    public LinkRenderer() {
        setTagName("a");
    }

    public LinkRenderer(Object item){
        this();
        add(item);
    }

    public LinkRenderer href(Renderer href){
        attribute("href", href);
        return this;
    }

    public LinkRenderer href(String href){
        return href(new StringRenderer(href));
    }
}
