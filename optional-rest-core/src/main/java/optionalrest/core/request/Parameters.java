package optionalrest.core.request;

import optionalrest.core.RestException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Parameters {
    protected Map<String, List<String>> map = new HashMap<>();

    public Parameters add(String key, String value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        addValue(key, value);
        return this;
    }

    public Parameters add(String key, Collection<String> values) {
        for (String value : values) {
            add(key, value);
        }
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
        if (!map.containsKey(key)){
            return null;
        }
        return map.get(key);
    }

    public Boolean getBooleanValue(String key){
        String value = getValue(key);
        return getBooleanValue(key, false);
    }

    public Boolean getBooleanValue(String key, Boolean defaultValue){
        String value = getValue(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public Integer getIntValue(String key, Integer defaultValue){
        String value = getValue(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public Integer getIntValue(String key){
        return getIntValue(key, null);
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

    public Collection<String> getKeys(){
        return map.keySet();
    }

    protected void addValue(String key, String value) {
        map.get(key).add(StringUtils.trim(value));
    }

    public Parameters clear(String key){
        map.put(key, new ArrayList<>());
        return this;
    }
}
