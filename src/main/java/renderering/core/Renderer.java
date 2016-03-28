package renderering.core;

import renderering.Situation;

public interface Renderer {
    String render();

    default String renderWithResources(Situation situation){
        return render();
    }
}
