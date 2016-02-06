package alainvanhout.renderering.renderer;

public interface CacheableRenderer extends Renderer {
    /**
     * Key to represent a specific rendering, meaning that for a given rendering any re-redender will produce the
     * exact same result. Returning null means that it it need not always be rendered exactly the same.
     *
     * @return
     */
    default String renderingKey(){
        return null;
    }
}
