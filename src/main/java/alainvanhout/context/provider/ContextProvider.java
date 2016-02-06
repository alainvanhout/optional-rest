package alainvanhout.context.provider;

import alainvanhout.routing.path.Path;

public interface ContextProvider {

    String getId();

    void handle(Path path);
}
