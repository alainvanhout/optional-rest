package alainvanhout.renderering.renderer.html;

import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.renderering.renderer.list.GenericListRenderer;
import alainvanhout.renderering.renderer.list.ListRenderer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class GenericElementRenderer implements ElementRenderer {

    protected String tagName;
    protected boolean selfClosing = false;
    protected AttributesRenderer attributes = new AttributesRenderer();
    protected GenericListRenderer<Object> contents = new GenericListRenderer<>()
            .includes(item -> {
                if (item instanceof String) {
                    return StringUtils.isNotBlank((String) item);
                } else {
                    return item != null;
                }
            });

    @Override
    public ListRenderer add(Object item) {
        if (item instanceof String) {
            add(new StringRenderer((String) item));
        } else {
            contents.add(item);
        }
        return this;
    }

    @Override
    public ListRenderer addAll(List<Object> items) {
        items.stream().forEach(item -> add(item));
        return this;
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    private String getStartTag(boolean close) {
        String attr = attributes.render();
        return getStartTagOpening() + tagName + (StringUtils.isNotBlank(attr) ? " " + attr : "") + getStartTagClosing(close);
    }

    private String getStartTagClosing(boolean close) {
        return close ? " />" : ">";
    }

    private String getStartTagOpening() {
        return "<";
    }

    @Override
    public String getEndTag() {
        return getEndTagOpening() + tagName + getStartTagClosing(false);
    }

    public String getEndTagOpening() {
        return "</";
    }

    @Override
    public String render() {
        String contents = this.contents.render();
        if (selfClosing && StringUtils.isBlank(contents)) {
            return getStartTag(true);
        } else {
            return getStartTag(false) + contents + getEndTag();
        }
    }

    @Override
    public ElementRenderer attribute(String name, Renderer value) {
        attributes.add(name, value);
        return this;
    }

    @Override
    public ElementRenderer attribute(String name, String value) {
        attributes.add(name, value);
        return this;
    }
}
