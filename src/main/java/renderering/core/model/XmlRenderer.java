package renderering.core.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import renderering.core.Renderer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

public class XmlRenderer implements Renderer {

    private Object model;

    public XmlRenderer(Object model) {
        this.model = model;
    }

    @Override
    public String render() {
        XStream xstream = new XStream();
        return xstream.toXML(model);
    }
}
