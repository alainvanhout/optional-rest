package alainvanhout.optionalrest.scope;

public class BuildParameters {
    private boolean asHtml;
    private boolean includeScopeId;

    public boolean getAsHtml() {
        return this.asHtml;
    }

    public BuildParameters asHtml(boolean asHtml) {
        this.asHtml = asHtml;
        return this;
    }

    public boolean getIncludeScopeId() {
        return this.includeScopeId;
    }

    public BuildParameters includeScopeId(boolean includeScopeId) {
        this.includeScopeId = includeScopeId;
        return this;
    }
}
