package renderering.web.html.basic.documenthead;

import renderering.web.html.GenericElementRenderer;

public class ScriptLinkRenderer extends GenericElementRenderer {

    public ScriptLinkRenderer(String src) {
        setTagName("script");
        attribute("src", src);
    }
}
