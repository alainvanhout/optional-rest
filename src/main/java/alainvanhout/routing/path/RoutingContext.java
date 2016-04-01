package alainvanhout.routing.path;

import context.Context;
import context.UpdateableContext;
import alainvanhout.cms.dtos.stored.StoredContext;
import context.ContextException;

import java.util.ArrayList;
import java.util.List;

public class RoutingContext implements UpdateableContext{

    private StoredContext context = new StoredContext();
    private List<Context> contexts = new ArrayList<>();

    public RoutingContext add(Context context) {
        contexts.add(context);
        return this;
    }

    @Override
    public UpdateableContext add(String key, Object value) {
        return context.add(key, value);
    }

    @Override
    public Object get(String key) {
        if (context.contains(key)){
            return context.get(key);
        }
        for (Context context : contexts) {
            if (context.contains(key)){
                return context.get(key);
            }
        }
        throw new ContextException("Could not find key " + key);
    }

    public boolean contains(String key) {
        if (context.contains(key)){
            return true;
        }
        for (Context context : contexts) {
            if (context.contains(key)){
                return true;
            }
        }
        return false;
    }
}
