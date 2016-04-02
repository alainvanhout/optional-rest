package context;

public interface TypedContext extends Context {

    default boolean contains(String key, Class clazz) {
        return contains(key) && ofCorrectType(get(key), clazz);
    }

    default boolean ofCorrectType(Object value, Class clazz) {
        return clazz.isInstance(value);
    }


}
