package renderering.core.context;

import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

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
