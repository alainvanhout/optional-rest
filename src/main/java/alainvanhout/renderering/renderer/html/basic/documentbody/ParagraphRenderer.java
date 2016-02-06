package alainvanhout.renderering.renderer.html.basic.documentbody;

import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class ParagraphRenderer extends GenericElementRenderer {

    public ParagraphRenderer() {
        setTagName("p");
    }

    public ParagraphRenderer(Object item){
        this();
        add(item);
    }
}
