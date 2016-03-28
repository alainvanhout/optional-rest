package renderering.web.html.basic.documentbody.select;

import renderering.RenderingException;
import renderering.core.Renderer;
import renderering.web.html.GenericElementRenderer;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class SelectRenderer extends GenericElementRenderer {


    public SelectRenderer() {
        setTagName("select");
    }

    public SelectRenderer addOption(Renderer value, Renderer text) {
        // TODO value
        if (text instanceof OptionRenderer) {
            contents.add(text);
        } else {
            contents.add(new OptionRenderer().add(text));
        }
        return this;
    }

    public SelectRenderer addOption(String value, String text) {
        // todo value
        contents.add(new OptionRenderer().add(text));
        return this;
    }

    public SelectRenderer addOptions(Object... renderers) {
        return addOptions(Arrays.asList(renderers));
    }

    public SelectRenderer addOptions(List<?> renderers) {
        for (Object renderer : renderers) {
            if (renderer instanceof OptionRenderer) {
                add((OptionRenderer) renderer);
            } else if (renderer instanceof Renderer) {
                addOption((Renderer) renderer, (Renderer) renderer);
            } else if (renderer instanceof String) {
                if (!(StringUtils.isBlank((String) renderer))) {
                    addOption((String) renderer, (String) renderer);
                }
            } else {
                throw new RenderingException("Cannot add option of class " + renderer.getClass());
            }
        }
        return this;
    }

}
