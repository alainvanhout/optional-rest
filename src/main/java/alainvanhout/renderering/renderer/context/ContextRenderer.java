package alainvanhout.renderering.renderer.context;

import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.basic.StringRenderer;

public interface ContextRenderer extends Renderer {
    Renderer get(String key);

    ContextRenderer set(String key, Renderer renderer);

    default ContextRenderer set(String key, String value){
        return set(key, new StringRenderer(value));
    };

    default ContextRenderer set(String key){
        return set(key, "");
    };

    boolean containsKey(String key);
}
