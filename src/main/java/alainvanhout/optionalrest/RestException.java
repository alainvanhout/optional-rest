package alainvanhout.optionalrest;

import java.util.HashMap;
import java.util.Map;

public class RestException extends RuntimeException {
    private Map<String, Object> context = new HashMap<>();

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException add(String key, Object value) {
        context.put(key, value);
        return this;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
