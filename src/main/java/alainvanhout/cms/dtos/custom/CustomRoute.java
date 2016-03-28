package alainvanhout.cms.dtos.custom;

import renderering.core.Renderer;
import alainvanhout.routing.Route;
import alainvanhout.routing.path.Path;

public interface CustomRoute extends Route {
    String getId();

    default Renderer follow(Path path) {
        if (path.done()) {
            return arrive(path);
        } else {
            return proceed(path);
        }
    }

    default Renderer arrive(Path path) {
        throw new UnsupportedOperationException();
    }

    default Renderer proceed(Path path) {
        throw new UnsupportedOperationException();
    }
}
