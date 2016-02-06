package alainvanhout.renderering.renderer.context;

import alainvanhout.context.RendererContext;
import alainvanhout.context.impl.SimpleRendererContext;
import alainvanhout.renderering.renderer.Renderer;


public class SimpleContextRenderer implements ContextRenderer {

    private Renderer body;
    private RendererContext context;

    public SimpleContextRenderer(Renderer body) {
        this.body = body;
        this.context = new SimpleRendererContext();
    }

    public SimpleContextRenderer(Renderer body, RendererContext context) {
        this.body = body;
        this.context = context;
    }

    @Override
    public String render() {
        String bodyText = body.render();
        String[] split = bodyText.split(getRegex());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String key = split[i];
            if (i % 2 == 0) {
                result.append(key);
            } else {
                if (context.contains(key)) {
                    result.append(context.getRenderer(key).render());
                } else {
                    throw new RuntimeException("Key not set for renderer: " + key);
                }
            }
        }
        return result.toString();
    }

    public String getRegex() {
        return "\\{{2}|\\}{2}";
    }

    @Override
    public Renderer get(String key) {
        return context.getRenderer(key);
    }

    @Override
    public ContextRenderer set(String key, Renderer renderer) {
        context.add(key, renderer);
        return this;
    }

    @Override
    public boolean containsKey(String key) {
        return context.contains(key);
    }
}
