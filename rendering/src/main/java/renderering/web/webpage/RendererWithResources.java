package renderering.web.webpage;

import renderering.Situation;
import renderering.core.Renderer;

public interface RendererWithResources extends Renderer {

    public String renderWithResources(Situation situation);
}
