package alainvanhout.renderering.renderer.html.basic.documenthead;

import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class ScriptLinkRenderer extends GenericElementRenderer {

    public ScriptLinkRenderer(String src) {
        setTagName("script");
        attribute("src", src);
    }
}
