package alainvanhout.optionalrest;

import alainvanhout.renderering.renderer.Renderer;

public class RestResponse {

    private int responseCode;
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

    public int getResponseCode() {
        return this.responseCode;
    }

    public RestResponse responseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public RestResponse setRenderer(Renderer renderer) {
        this.renderer = renderer;
        return this;
    }
}
