package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.scope.Supported;

public abstract class BasicMapping implements Mapping {

    private Supported supported = new Supported();

    @Override
    public Supported getSupported() {
        return this.supported;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + getSupported().toString();
    }
}
