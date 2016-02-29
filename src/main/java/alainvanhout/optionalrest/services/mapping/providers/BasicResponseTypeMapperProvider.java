package alainvanhout.optionalrest.services.mapping.providers;

import alainvanhout.optionalrest.response.RendererResponse;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class BasicResponseTypeMapperProvider implements ResponseTypeMapperProvider {

    @Override
    public Map<Class, Function<Object, Object>> getResponseTypeMappers() {
        Map<Class, Function<Object, Object>> map = new HashMap<>();

        map.put(Renderer.class, r -> new RendererResponse().renderer((Renderer) r));
        map.put(String.class, r -> new StringRenderer((String) r));

        return map;
    }
}
