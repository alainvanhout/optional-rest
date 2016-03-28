package optionalrest.rendering;

import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import optionalrest.core.services.mapping.providers.ResponseConverterProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class BasicResponseConverterProvider implements ResponseConverterProvider {

    @Override
    public Map<Class, Function<Object, Object>> getResponseConverters() {
        Map<Class, Function<Object, Object>> map = new HashMap<>();

        map.put(Renderer.class, r -> new RendererResponse().renderer((Renderer) r));
        map.put(String.class, r -> new StringRenderer((String) r));

        return map;
    }


}
