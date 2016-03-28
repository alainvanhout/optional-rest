package alainvanhout.sections;

import renderering.core.Renderer;
import alainvanhout.routing.path.Path;

public interface Section {

    String getId();

    Renderer getRenderer(Path context);
}
