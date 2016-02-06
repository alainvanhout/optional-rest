package alainvanhout.renderering.renderer.html.basic.documentbody.list;

import alainvanhout.renderering.renderer.html.GenericElementRenderer;

public class UnorderedListRenderer extends GenericElementRenderer {
    private boolean _ignoreNull;
    private boolean _ignoreBlank;

    public UnorderedListRenderer() {
        setTagName("ul");
        contents.postProcess(renderer -> {
            if (renderer instanceof ListItemRenderer){
                return renderer;
            } else {
                return new ListItemRenderer(renderer);
            }
        });
    }

//    public UnorderedListRenderer addItems(List<?> renderers) {
//        for (Object renderer : renderers) {
//            if (renderer == null) {
//                if (!_ignoreNull && !_ignoreBlank) {
//                    throw new RenderingException("Cannot add null item");
//                }
//            } else if (renderer instanceof ListItemRenderer) {
//                add((ListItemRenderer) renderer);
//            } else if (renderer instanceof Renderer) {
//                addItem((Renderer) renderer);
//            } else if (renderer instanceof String) {
//                if (!(StringUtils.isBlank((String) renderer)) && _ignoreBlank) {
//                    addItem((String) renderer);
//                }
//            } else {
//                throw new RenderingException("Cannot add item of class " + renderer.getClass());
//            }
//        }
//        return this;
//    }

    public boolean isIgnoreNull() {
        return _ignoreNull;
    }

    public UnorderedListRenderer ignoreNull(boolean ignoreNull) {
        this._ignoreNull = ignoreNull;
        return this;
    }

    public boolean isIgnoreBlank() {
        return _ignoreBlank;
    }

    public UnorderedListRenderer ignoreBlank(boolean ignoreBlank) {
        this._ignoreBlank = ignoreBlank;
        return this;
    }
}
