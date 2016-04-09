package optionalrest.cms.resources;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Resource {
    private String id;
    Map<String, String> parameters = new HashMap<>();

    public String getId() {
        return this.id;
    }

    public Resource id(String id) {
        this.id = id;
        return this;
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }

    public Resource parameter(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Resource && StringUtils.equals(((Resource) obj).getId(), id);
    }
}
