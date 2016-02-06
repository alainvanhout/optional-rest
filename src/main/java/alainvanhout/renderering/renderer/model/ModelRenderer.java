package alainvanhout.renderering.renderer.model;

import alainvanhout.renderering.renderer.CacheableRenderer;
import alainvanhout.renderering.renderer.Renderer;

public interface ModelRenderer<T> extends Renderer, CacheableRenderer {

    ModelRenderer<T> set(T object);
}
