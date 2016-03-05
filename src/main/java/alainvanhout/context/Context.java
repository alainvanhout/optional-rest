package alainvanhout.context;

public interface Context {

    default <T> T get(String key) {
        return (T) get(key);
    }

    boolean contains(String key);
}
