package context;

import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

// TODO move this outside context package or remove
public interface RendererContext extends UpdateableContext, TypedContext {
    default Renderer getRenderer(String key) {
        Object item = get(key);
        if (item instanceof Renderer) {
            return (Renderer) item;
        }
        if (item instanceof String) {
            return new StringRenderer((String) item);
        }
        throw new ContextException("No value found for key " + key);
    }

    @Override
    default boolean ofCorrectType(Object value, Class clazz) {
        return String.class.isInstance(value) || Renderer.class.isInstance(value);
    }

    @Override
    default UpdateableContext add(String key, Object value) {
        throw new UnsupportedOperationException();
    }
}
