package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.scope.Supported;

import java.util.*;

public abstract class BasicMapping implements Mapping {

    private Map<String, Set<Object>> map = new HashMap<>();
    private Supported supported;

    @Override
    public Supported getSupported() {
        return this.supported;
    }

    @Override
    public Mapping supported(Supported supported) {
        this.supported = supported;
        return this;
    }
}
