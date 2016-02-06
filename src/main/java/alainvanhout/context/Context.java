package alainvanhout.context;

public interface Context {

    Object get(String key);

    default <T> T getAs(String key) {
        return (T) get(key);
    }

    boolean contains(String key);
}
