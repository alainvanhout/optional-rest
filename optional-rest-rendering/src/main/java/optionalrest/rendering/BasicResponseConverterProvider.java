package optionalrest.rendering;

import optionalrest.core.response.RendererResponse;
import optionalrest.core.services.mapping.providers.ResponseConverterProvider;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BasicResponseConverterProvider implements ResponseConverterProvider {

    @Override
    public Map<Class, Function<Object, Object>> getResponseConverters() {
        Map<Class, Function<Object, Object>> map = new HashMap<>();

        map.put(Renderer.class, r -> new RendererResponse().renderer((Renderer) r));
        map.put(String.class, r -> new StringRenderer((String) r));

        return map;
    }
}
