package alainvanhout.optionalrest.services.mapping;

import alainvanhout.optionalrest.scope.Supported;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BasicMapping implements Mapping {

    private Map<String, Set<Object>> map = new HashMap<>();
    private Supported supported = new Supported();

    @Override
    public Supported getSupported() {
        return this.supported;
    }
}
