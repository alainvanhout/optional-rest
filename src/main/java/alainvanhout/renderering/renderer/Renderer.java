package alainvanhout.renderering.renderer;

import alainvanhout.renderering.Situation;

public interface Renderer {
    String render();

    default String renderWithResources(Situation situation){
        return render();
    }
}
