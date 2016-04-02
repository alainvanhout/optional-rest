package renderering.web.html;

import renderering.core.Renderer;
import renderering.core.list.ListRenderer;

import java.util.List;

public interface ElementRenderer extends ListRenderer<Object> {
    @Override
    ListRenderer add(Object item);

    @Override
    ListRenderer addAll(List<Object> items);

    default ListRenderer addItems(List items){
        for (Object item : items) {
            add(item);
        }
        return this;
    }

    String getTagName();

    void setTagName(String tagName);

    String getEndTag();

    @Override
    String render();

    ElementRenderer attribute(String name, Renderer value);

    ElementRenderer attribute(String name, String value);
}
