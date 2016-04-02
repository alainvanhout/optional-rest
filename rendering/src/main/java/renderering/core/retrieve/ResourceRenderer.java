package renderering.core.retrieve;

import renderering.core.CacheableRenderer;
import renderering.core.Renderer;

public abstract class ResourceRenderer implements Renderer, CacheableRenderer {
    protected String resource;

    public ResourceRenderer() {
    }

    public ResourceRenderer(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String render() {
        return fetchText();
    }

    @Override
    public String renderingKey() {
        if (resource != null){
            return resource;
        }
        return null;
    }

    public abstract String fetchText();
}
