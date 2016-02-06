package alainvanhout.renderering.renderer.webpage;

import alainvanhout.renderering.Situation;
import alainvanhout.renderering.renderer.Renderer;

public interface RendererWithResources extends Renderer {

    public String renderWithResources(Situation situation);
}
