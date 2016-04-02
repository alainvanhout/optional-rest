package renderering.core;

import renderering.core.model.ModelRenderer;

public interface DelegatingRenderer<T> extends Renderer {

    DelegatingRenderer use(ModelRenderer<T> renderer);

}
