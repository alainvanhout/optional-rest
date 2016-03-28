package renderering.core;

public interface TypedRenderer<T> extends Renderer {

    TypedRenderer set(T renderer);

    T get();
}
