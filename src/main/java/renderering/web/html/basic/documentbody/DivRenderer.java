package renderering.web.html.basic.documentbody;

import renderering.web.html.GenericElementRenderer;

public class DivRenderer extends GenericElementRenderer {

    public DivRenderer() {
        setTagName("div");
    }

    public DivRenderer(Object item){
        this();
        add(item);
    }
}
