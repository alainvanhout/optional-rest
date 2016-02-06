package alainvanhout.renderering.renderer.basic;

import alainvanhout.renderering.renderer.Renderer;
import org.apache.commons.lang3.StringUtils;

public class StringRenderer implements Renderer {

    private String content;

    public StringRenderer() {
    }
    public StringRenderer(String content) {
        this.content = content;
    }

    @Override
    public String render() {
        return StringUtils.defaultString(content);
    }
}
