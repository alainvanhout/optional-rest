package renderering.web.html.basic.documentbody;

import renderering.web.html.GenericElementRenderer;

public class PreRenderer extends GenericElementRenderer {

    public PreRenderer() {
        setTagName("pre");
    }

    public PreRenderer(Object item){
        this();
        add(item);
    }
}
