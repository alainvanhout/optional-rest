package optionalrest.rendering;

import com.thoughtworks.xstream.XStream;
import renderering.core.Renderer;

public class XmlRenderer implements Renderer {

    private Object model;

    public XmlRenderer(Object model) {
        this.model = model;
    }

    @Override
    public String render() {
        XStream xstream = new XStream();
        xstream.alias(model.getClass().getSimpleName(), model.getClass());
        return xstream.toXML(model);
    }
}
