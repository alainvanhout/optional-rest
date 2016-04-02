package context.impl;

import context.UpdateableContext;
import org.omg.SendingContext.RunTime;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleModelContext<T> implements UpdateableContext {

    private final T model;
    private List<String> fieldNames;

    public SimpleModelContext(T model) {
        this.model = model;
        fieldNames = Arrays.asList(model.getClass().getDeclaredFields()).stream().map(Field::getName).collect(Collectors.toList());
    }

    @Override
    public UpdateableContext add(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(String key) {
        try {
            Field field = model.getClass().getDeclaredField(key);
            field.setAccessible(true);
            return (T) field.get(model).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("An error occurred while accessing field %s of class %s", key, model.getClass().getCanonicalName()), e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(String.format("An error occurred while accessing field %s of class %s", key, model.getClass().getCanonicalName()), e);
        }
    }

    @Override
    public boolean contains(String key) {
        return fieldNames.contains(key);
    }
}
