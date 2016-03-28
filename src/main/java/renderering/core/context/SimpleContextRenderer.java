package renderering.core.context;

import alainvanhout.context.Context;
import alainvanhout.context.UpdateableContext;
import alainvanhout.context.impl.SimpleRendererContext;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;


public class SimpleContextRenderer implements ContextRenderer {

    private Renderer body;
    private Context context;

    public SimpleContextRenderer(Renderer body) {
        this.body = body;
        this.context = new SimpleRendererContext();
    }

    public SimpleContextRenderer(Renderer body, Context context) {
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
                    Object content = context.get(key);
                    if (content instanceof Renderer){
                        result.append(((Renderer)content).render());
                    } else if (content instanceof String){
                        result.append((String)content);
                    } else {
                        throw new RuntimeException("Value is neither a string nor a renderer" + key);
                    }
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
        return new StringRenderer(context.get(key));
    }

    @Override
    public ContextRenderer set(String key, Renderer renderer) {
        ((UpdateableContext) context).add(key, renderer);
        return this;
    }

    @Override
    public boolean containsKey(String key) {
        return context.contains(key);
    }
}
