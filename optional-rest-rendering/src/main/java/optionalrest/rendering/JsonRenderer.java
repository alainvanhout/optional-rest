package optionalrest.rendering;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

public class JsonRenderer implements Renderer {

    private Object model;
    private Gson gson;

    public JsonRenderer(Object model) {
        this.model = model;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String render() {
        return gson.toJson(model);
    }
}
