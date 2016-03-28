package renderering.web.html.basic.documentbody;

import renderering.web.html.GenericElementRenderer;

public class SpanRenderer extends GenericElementRenderer {

    public SpanRenderer() {
        setTagName("span");
    }

    public SpanRenderer(Object item){
        this();
        add(item);
    }

}
