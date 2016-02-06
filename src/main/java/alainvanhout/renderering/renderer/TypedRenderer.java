package alainvanhout.renderering.renderer;

public interface TypedRenderer<T> extends Renderer {

    TypedRenderer set(T renderer);

    T get();
}
