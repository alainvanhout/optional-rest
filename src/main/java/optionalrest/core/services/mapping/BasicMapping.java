package optionalrest.core.services.mapping;

import optionalrest.core.scope.Supported;

public abstract class BasicMapping implements Mapping {

    private boolean passing;
    private int order;
    private Supported supported = new Supported();

    @Override
    public boolean isPassing() {
        return passing;
    }

    public BasicMapping passing(boolean passing) {
        this.passing = passing;
        return this;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public BasicMapping order(int order) {
        this.order = order;
        return this;
    }

    public BasicMapping incrementOrder(int order) {
        this.order += order;
        return this;
    }

    @Override
    public Supported getSupported() {
        return this.supported;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + getSupported().toString();
    }
}
