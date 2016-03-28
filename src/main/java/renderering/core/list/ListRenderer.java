package renderering.core.list;

import renderering.core.Renderer;

import java.util.List;

public interface ListRenderer<T> extends Renderer {

    ListRenderer add(T item);

    ListRenderer addAll(List<T> item);

    default ListRenderer addAny(Object item) {
        if (item instanceof List) {
            addAll((List<T>) item);
        } else {
            add((T) item);
        }
        return this;
    }
}
