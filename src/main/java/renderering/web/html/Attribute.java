package renderering.web.html;

import renderering.core.Renderer;

public class Attribute {
    private String name;
    private Renderer value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Renderer getValue() {
        return value;
    }

    public void setValue(Renderer value) {
        this.value = value;
    }
}
