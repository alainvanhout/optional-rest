package alainvanhout.renderering.renderer.list;

import alainvanhout.renderering.renderer.Renderer;

import java.util.List;
import java.util.function.Function;

public class SimpleListRenderer implements ListRenderer<Renderer> {

    private GenericListRenderer<Renderer> listRenderer = new GenericListRenderer<>();

    public GenericListRenderer<Renderer> postProcess(Function<Renderer, Renderer> postProcess) {
        return listRenderer.postProcess(postProcess);
    }

    public GenericListRenderer<Renderer> includes(Function<Renderer, Boolean> includes) {
        return listRenderer.includes(includes);
    }

    public GenericListRenderer<Renderer> preProcess(Function<Renderer, Renderer> preProcess) {
        return listRenderer.preProcess(preProcess);
    }

    public GenericListRenderer<Renderer> delimiter(String delimiter) {
        return listRenderer.delimiter(delimiter);
    }

    public GenericListRenderer<Renderer> wrap(Function<String, String> wrap) {
        return listRenderer.wrap(wrap);
    }

    @Override
    public ListRenderer add(Renderer item) {
        return listRenderer.add(item);
    }

    @Override
    public ListRenderer addAll(List<Renderer> items) {
        return listRenderer.addAll(items);
    }

    @Override
    public String render() {
        return listRenderer.render();
    }
}
