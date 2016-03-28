package renderering.core.manage;

import renderering.core.CacheableRenderer;
import renderering.core.Renderer;

import java.util.HashMap;
import java.util.Map;

public class CachingRenderer implements Renderer {

    private CacheableRenderer content;
    private Map<String, String> cache = new HashMap<>();

    public CachingRenderer(CacheableRenderer content) {
        this.content = content;
    }

    public void clear(){
        cache.clear();
    }

    @Override
    public String render() {
        String key = content.renderingKey();
        // try to use cache
        if (key != null && cache.containsKey(key)) {
            System.out.println("Using cache: " + key);
            return cache.get(key);
        }

        String rendering = content.render();
        // try to add to cache
        if (key != null){
            System.out.println("Added to cache: " + key);
            cache.put(key, rendering);
        }
        return rendering;
    }
}
