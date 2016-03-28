package renderering.web.html.basic.documenthead;

import renderering.web.html.GenericElementRenderer;

public class StylesheetLinkRenderer extends GenericElementRenderer {

    public StylesheetLinkRenderer(String href) {
        setTagName("link");
        attribute("rel", "stylesheet");
        attribute("href", href);
        selfClosing = true;
    }
}
