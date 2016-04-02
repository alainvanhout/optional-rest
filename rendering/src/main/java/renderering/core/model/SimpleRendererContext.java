package renderering.core.model;

import context.ContextException;
import context.UpdateableContext;
import context.impl.MapContext;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

public class SimpleRendererContext implements RendererContext, UpdateableContext {
    private UpdateableContext context;

    public SimpleRendererContext() {
        context = new MapContext();
    }

    public SimpleRendererContext(UpdateableContext context) {
        this.context = context;
    }

    @Override
    public UpdateableContext add(String key, Object value) {
        return context.add(key, value);
    }

    @Override
    public boolean contains(String key, Class clazz) {
        return context.contains(key);
    }

    @Override
    public Renderer getRenderer(String key) {
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
    public Object get(String key) {
        return context.get(key);
    }

    @Override
    public boolean contains(String key) {
        return context.contains(key);
    }
}
