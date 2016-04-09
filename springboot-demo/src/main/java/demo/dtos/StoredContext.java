package demo.dtos;

import context.UpdateableContext;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "context")
public class StoredContext implements UpdateableContext {

    private Map<String, String> map = new HashMap<>();

    @Override
    public UpdateableContext add(String key, Object value) {
        map.put(key, (String)value);
        return this;
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }
}
