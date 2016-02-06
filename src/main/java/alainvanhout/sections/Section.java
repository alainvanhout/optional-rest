package alainvanhout.sections;

import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.routing.path.Path;

public interface Section {

    String getId();

    Renderer getRenderer(Path context);
}
