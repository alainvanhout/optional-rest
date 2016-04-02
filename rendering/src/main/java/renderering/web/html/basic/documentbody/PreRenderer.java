package renderering.web.html.basic.documentbody;

import org.apache.commons.lang3.StringEscapeUtils;
import renderering.web.html.GenericElementRenderer;

public class PreRenderer extends GenericElementRenderer {

    public PreRenderer() {
        setTagName("pre");
    }

    public PreRenderer(Object item){
        this();
        add(item);
    }

    @Override
    public String render() {
        return super.render();
    }
}
