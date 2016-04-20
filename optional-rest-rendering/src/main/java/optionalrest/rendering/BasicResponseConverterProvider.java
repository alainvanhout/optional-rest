package optionalrest.rendering;

import optionalrest.core.services.mapping.providers.responses.ResponseConverter;
import optionalrest.core.services.mapping.providers.responses.ResponseConverterProvider;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

import java.util.HashMap;
import java.util.Map;

public class BasicResponseConverterProvider implements ResponseConverterProvider {

    @Override
    public Map<Class, ResponseConverter> defineConvertersForClass() {
        Map<Class, ResponseConverter> map = new HashMap<>();

        map.put(Renderer.class, r -> new RendererResponse().renderer((Renderer) r));
        map.put(String.class, r -> new StringRenderer((String) r));

        return map;
    }
}
