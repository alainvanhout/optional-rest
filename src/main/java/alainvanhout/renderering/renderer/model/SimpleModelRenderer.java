package alainvanhout.renderering.renderer.model;

import alainvanhout.renderering.RenderingException;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.context.ContextRenderer;
import alainvanhout.renderering.renderer.context.SimpleContextRenderer;

import java.lang.reflect.Field;

public class SimpleModelRenderer<T> implements ModelRenderer<T> {

    private Renderer body;
    private T model;

    public SimpleModelRenderer(Renderer body) {
        this.body = body;
    }

    public T get() {
        return model;
    }

    public SimpleModelRenderer set(T model) {
        this.model = model;
        return this;
    }

    @Override
    public String render() {
        ContextRenderer paramBody = new SimpleContextRenderer(body);
        for (Field field : model.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                paramBody.set(field.getName(), field.get(model).toString());
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RenderingException(String.format("An error occurred while accessing field %s of class %s", field.getName(), model.getClass().getCanonicalName()), e);
            }
        }
        return paramBody.render();
    }

    @Override
    public String renderingKey() {
        if (model != null){
            return this.getClass().getCanonicalName() + "-" + model.getClass().getCanonicalName() + "-" + model.toString();
        }
        return null;
    }
}
