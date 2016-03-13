package alainvanhout.optionalrest.scope.definition;

import org.apache.commons.lang3.ObjectUtils;

public class ScopeDefinition {
    private String name;
    private String type;
    private String description;
    private Class internalClass;

    public String getDescription() {
        return description;
    }

    public ScopeDefinition description(String description) {
        this.description = description;
        return this;
    }

    public String getType() {
        return type;
    }

    public ScopeDefinition type(String type) {
        this.type = type;
        return this;
    }

    public ScopeDefinition name(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public Class getInternalClass() {
        return internalClass;
    }

    public void setInternalClass(Class internalClass) {
        this.internalClass = internalClass;
    }

    @Override
    public String toString() {
        return ObjectUtils.firstNonNull(name, internalClass, "").toString();
    }
}
