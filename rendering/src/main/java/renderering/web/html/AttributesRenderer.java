package renderering.web.html;

import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributesRenderer implements Renderer {

    private Map<String, Renderer> attributes = new HashMap<>();

    public AttributesRenderer() {
    }

    public AttributesRenderer add(String name, Renderer value) {
        attributes.put(name, value);
        return this;
    }

    public AttributesRenderer add(String name, String value) {
        attributes.put(name, new StringRenderer(value));
        return this;
    }

    public AttributesRenderer clear(){
        attributes.clear();
        return this;
    }

    @Override
    public String render() {
        return attributes.entrySet().stream().map(attr -> attr.getKey() + "=" + "'" + attr.getValue().render() + "'").collect(Collectors.joining(" "));
    }
}
