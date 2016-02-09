package alainvanhout.rest.request;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Headers {
    private static final List<String> COMMA_SEPARATED = Arrays.asList("accept", "accept-encoding", "accept-language");

    Map<String, List<String>> headers = new HashMap<>();

    public void add(String key, String value) {
        if (!headers.containsKey(key)) {
            headers.put(key, new ArrayList<>());
        }

        if (COMMA_SEPARATED.contains(key)) {
            String[] split = value.split(",");
            for (String headerValue : split) {
                addValue(key, headerValue);
            }
        } else {
            addValue(key, value);
        }
    }

    private void addValue(String key, String value){
        headers.get(key).add(StringUtils.trim(value));
    }

    public boolean hasHeader(String key, String value) {
        return headers.containsKey(key) && headers.get(key).contains(value);
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key);
    }
}
