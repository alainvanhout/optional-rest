package renderering.core.model;

import renderering.core.CacheableRenderer;
import renderering.core.Renderer;

public interface ModelRenderer<T> extends Renderer, CacheableRenderer {

    ModelRenderer<T> set(T object);
}
