package alainvanhout.context.impl;

import alainvanhout.context.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListContext implements Context {

    private List<Context> contexts = new ArrayList<>();

    public ListContext(Context... contexts) {
        this.contexts.addAll(Arrays.asList(contexts));
    }

    @Override
    public <T> T get(String key) {
        for (Context context : contexts) {
            if (context.contains(key)){
                return (T)context.get(key);
            }
        }
        return null;
    }

    @Override
    public boolean contains(String key) {
        for (Context context : contexts) {
            if (context.contains(key)){
                return true;
            }
        }
        return false;
    }

    public ListContext add(Context context){
        contexts.add(context);
        return this;
    }
}
