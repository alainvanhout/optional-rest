package renderering.web.html.basic.documentbody;

import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;
import renderering.web.html.GenericElementRenderer;

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
