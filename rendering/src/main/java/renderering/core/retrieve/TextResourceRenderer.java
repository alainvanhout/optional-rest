package renderering.core.retrieve;

import renderering.RenderingException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class TextResourceRenderer extends ResourceRenderer {

    public TextResourceRenderer(String resource) {
        super(resource);
    }

    @Override
    public String fetchText() {
        try {
            InputStream resourceAsStream = TextResourceRenderer.class.getResourceAsStream("/" + resource);
            if (resourceAsStream == null){
                throw new RenderingException("Resource unavailable:" + resource);
            }
            return IOUtils.toString(resourceAsStream);
        } catch (IOException e) {
            throw new RenderingException("Unable to access resource: " + resource, e);
        }
    }

}
