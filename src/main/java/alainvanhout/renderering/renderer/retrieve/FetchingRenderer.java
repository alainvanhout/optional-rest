package alainvanhout.renderering.renderer.retrieve;

import alainvanhout.renderering.renderer.Renderer;

public class FetchingRenderer<T> implements Renderer {
    private Fetcher fetcher;
    private T resource;

    public FetchingRenderer(Fetcher fetcher, T resource) {
        this.fetcher = fetcher;
        this.resource = resource;
    }

    @Override
    public String render() {
       return fetcher.fetch(resource).render();
    }

    public interface Fetcher<T>{
        Renderer fetch(T resource);
    }
}
