package optionalrest.cms.contexts;

import context.UpdateableContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalContext implements UpdateableContext, CustomContext{

    private Map<String, Object> map = new HashMap<>();

    @PostConstruct
    private void setup(){
        add("applicationName", "my test application");
    }

    @Override
    public String getId() {
        return "global";
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
