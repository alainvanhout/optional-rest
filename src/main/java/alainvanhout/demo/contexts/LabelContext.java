package alainvanhout.demo.contexts;

import alainvanhout.cms.dtos.custom.CustomContext;
import context.UpdateableContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class LabelContext implements UpdateableContext, CustomContext {

    private Map<String, Object> map = new HashMap<>();

    @PostConstruct
    private void setup() {
        add("foo", "bar");
    }

    @Override
    public String getId() {
        return "label";
    }

    @Override
    public UpdateableContext add(String key, Object value) {
        map.put(key, value);
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
