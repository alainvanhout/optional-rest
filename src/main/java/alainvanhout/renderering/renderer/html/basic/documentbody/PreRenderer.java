package alainvanhout.renderering.renderer.html.basic.documentbody;

import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class PreRenderer extends GenericElementRenderer {

    public PreRenderer() {
        setTagName("pre");
    }

    public PreRenderer(Object item){
        this();
        add(item);
    }
}
