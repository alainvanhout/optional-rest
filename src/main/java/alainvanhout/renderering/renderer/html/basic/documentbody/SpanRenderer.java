package alainvanhout.renderering.renderer.html.basic.documentbody;

import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class SpanRenderer extends GenericElementRenderer {

    public SpanRenderer() {
        setTagName("span");
    }

    public SpanRenderer(Object item){
        this();
        add(item);
    }

}
