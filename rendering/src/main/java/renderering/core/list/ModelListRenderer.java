package renderering.core.list;

import renderering.core.model.SimpleModelRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelListRenderer<T> implements ListRenderer<T> {

    private List<T> items = new ArrayList<>();
    private SimpleModelRenderer<T> renderer;
    protected boolean _ignoreNull;

    public ModelListRenderer(SimpleModelRenderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public ListRenderer add(T item) {
        items.add(item);
        return this;
    }

    @Override
    public ListRenderer addAll(List<T> item) {
        items.addAll(items);
        return this;
    }

    @Override
    public String render() {
        return items.stream().map(item -> {
            renderer.set(item);
            return renderer.render();
        }).collect(Collectors.joining(getDelimiter()));
    }

    protected String getDelimiter() {
        return "";
    }

    public boolean isIgnoreNull() {
        return _ignoreNull;
    }

    public ModelListRenderer<T> ignoreNull(boolean ignoreNull) {
        this._ignoreNull = ignoreNull;
        return this;
    }
}
