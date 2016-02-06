package alainvanhout.renderering.renderer;

import alainvanhout.renderering.renderer.model.ModelRenderer;

public interface DelegatingRenderer<T> extends Renderer {

    DelegatingRenderer use(ModelRenderer<T> renderer);

}
