package optionalrest.core.scope;

import java.util.Map;

public class ScopeDto {
    private String id;
    private String parent;
    private String instance;
    private Map<String, String> relative;

    public String getId() {
        return this.id;
    }

    public ScopeDto id(String id) {
        this.id = id;
        return this;
    }

    public String getInstance() {
        return this.instance;
    }

    public ScopeDto instance(String instance) {
        this.instance = instance;
        return this;
    }

    public Map<String, String> getRelative() {
        return this.relative;
    }

    public ScopeDto relative(Map<String, String> relative) {
        this.relative = relative;
        return this;
    }

    public String getParent() {
        return parent;
    }

    public ScopeDto parent(String parent) {
        this.parent = parent;
        return this;
    }
}
