package alainvanhout.renderering.renderer.list;

import alainvanhout.renderering.RenderingException;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.TypedRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericListRenderer<T> implements ListRenderer<T>, TypedRenderer<List<T>> {

    protected List<T> items = new ArrayList<T>();
    protected String delimiter = "";
    protected Function<String, String> wrap = (rendering) -> rendering;
    protected Function<T, Boolean> includes = (item) -> true;
    protected Function<Renderer, Renderer> postProcess = (renderer) -> renderer;
    protected Function<T, Renderer> preProcess = (item) -> {
        if (!(item instanceof Renderer)) {
            throw new RenderingException("Basic preprocessing requires a renderer as input instead of a " + item.getClass());
        }
        return (Renderer) item;
    };

    public GenericListRenderer() {
    }

    public GenericListRenderer(List<T> items) {
        set(items);
    }

    @Override
    public ListRenderer add(T item) {
        items.add(item);
        return this;
    }

    @Override
    public ListRenderer addAll(List<T> items) {
        this.items.addAll(items);
        return this;
    }

    @Override
    public TypedRenderer set(List<T> renderer) {
        items = renderer;
        return this;
    }

    @Override
    public List<T> get() {
        return items;
    }

    @Override
    public String render() {
        String rendering = items.stream()
                .filter(item -> includes.apply(item))
                .map(item -> {
                    Renderer itemAsRenderer = preProcess.apply(item);
                    Renderer finalRenderer = postProcess.apply(itemAsRenderer);
                    return finalRenderer.render();
                }).collect(Collectors.joining(delimiter));
        return wrap.apply(rendering);
    }

    public GenericListRenderer<T> delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public GenericListRenderer<T> wrap(Function<String, String> wrap) {
        this.wrap = wrap;
        return this;
    }

    public GenericListRenderer<T> includes(Function<T, Boolean> includes) {
        this.includes = includes;
        return this;
    }

    public GenericListRenderer<T> postProcess(Function<Renderer, Renderer> postProcess) {
        this.postProcess = postProcess;
        return this;
    }

    public GenericListRenderer<T> preProcess(Function<T, Renderer> preProcess) {
        this.preProcess = preProcess;
        return this;
    }
}
