package alainvanhout.optionalrest;

import alainvanhout.renderering.renderer.Renderer;

public class RestResponse {

    private Renderer renderer;

    public String render() {
        return renderer.render();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public RestResponse renderer(Renderer renderer) {
        this.renderer = renderer;
        return this;
    }
}
