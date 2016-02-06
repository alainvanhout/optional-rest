package alainvanhout.renderering.renderer.html.basic.documentbody;

import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class DivRenderer extends GenericElementRenderer {

    public DivRenderer() {
        setTagName("div");
    }

    public DivRenderer(Object item){
        this();
        add(item);
    }
}
