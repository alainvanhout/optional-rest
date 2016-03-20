package alainvanhout.context;

public interface Context {

    /**
     * @param key The key that identifies what is to be retrieved
     * @param <T> The assumed type of what is to be retrieved (implementations will try to cast to that type)
     * @return The value that related to the given key
     */
    default <T> T get(String key) {
        return null;
    }

    /**
     * @param key Key that potentially relates to a value which the context could supply
     * @return Whether the key is known to the context
     */
    boolean contains(String key);
}
