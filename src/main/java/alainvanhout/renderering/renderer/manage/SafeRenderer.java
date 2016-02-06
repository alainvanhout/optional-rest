package alainvanhout.renderering.renderer.manage;

import alainvanhout.renderering.renderer.Renderer;

public class SafeRenderer implements Renderer {

    final private Renderer content;
    private Handler handler;

    public SafeRenderer(Renderer content) {
        this.content = content;
    }

    // TODO: add an alternatives that takes a lambda function which is inserted as Handler::doReturn or Handler::handleExcepion
    public SafeRenderer(Renderer content, Handler handler) {
        this.content = content;
        this.handler = handler;
    }

    @Override
    public String render() {
        try {
            return content.render();
        } catch (Exception e){
            if (handler != null){
                handler.handleException(content, e);
                return handler.thenReturn(content, e);
            }
        }
        return null;
    }

    public interface Handler {
        default void handleException(Renderer renderer, Exception e){};

        default String thenReturn(Renderer renderer, Exception e){
            return null;
        };
    }
}
