package renderering.web.html.basic.documentbody.list;

import renderering.web.html.GenericElementRenderer;

import java.util.List;

public class ListItemRenderer extends GenericElementRenderer {

    public ListItemRenderer() {
        setTagName("li");
    }

    public ListItemRenderer(Object item) {
        this();
        if (item instanceof List){
            contents.addAll((List)item);
        } else {
            contents.add(item);
        }
    }
}
