package alainvanhout.optionalrest.response;

import alainvanhout.renderering.renderer.Renderer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class RendererResponse extends BasicResponse {

    private Renderer renderer;

    public String render() {
        return renderer.render();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public RendererResponse renderer(Renderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public RendererResponse setRenderer(Renderer renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    public InputStream toStream() {
        return new ByteArrayInputStream(render().getBytes());
    }
}