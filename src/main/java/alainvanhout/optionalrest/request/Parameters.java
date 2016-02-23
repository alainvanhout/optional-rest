package alainvanhout.optionalrest.request;

import alainvanhout.optionalrest.RestException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameters {
    protected Map<String, List<String>> map = new HashMap<>();

    public Parameters add(String key, String value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        addValue(key, value);
        return this;
    }

    public Parameters add(Map<String, String> parameters){
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Parameters addAll(Map<String, String[]> parameters){
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            for (String value : entry.getValue()) {
                add(entry.getKey(), value);
            }
        }
        return this;
    }


    public boolean contains(String key, String value) {
        return map.containsKey(key) && map.get(key).contains(value);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public List<String> get(String key){
        // TODO exception
        return map.get(key);
    }

    public String getValue(String key){
        List<String> values = map.get(key);
        if (values == null) {
            return null;
        }
        if (values.size() != 1){
            throw new RestException("More tha one value found for key " + key);
        }
        return values.get(0);
    }

    protected void addValue(String key, String value) {
        map.get(key).add(StringUtils.trim(value));
    }
}
