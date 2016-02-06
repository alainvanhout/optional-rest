package alainvanhout.renderering.renderer.html.basic.documenthead;

import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class StylesheetLinkRenderer extends GenericElementRenderer {

    public StylesheetLinkRenderer(String href) {
        setTagName("link");
        attribute("rel", "stylesheet");
        attribute("href", href);
        selfClosing = true;
    }
}
