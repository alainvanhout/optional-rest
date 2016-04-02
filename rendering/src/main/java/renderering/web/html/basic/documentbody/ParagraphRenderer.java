package renderering.web.html.basic.documentbody;

import renderering.web.html.GenericElementRenderer;

public class ParagraphRenderer extends GenericElementRenderer {

    public ParagraphRenderer() {
        setTagName("p");
    }

    public ParagraphRenderer(Object item){
        this();
        add(item);
    }
}
