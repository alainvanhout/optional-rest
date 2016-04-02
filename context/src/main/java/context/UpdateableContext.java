package context;

public interface UpdateableContext extends Context {

    UpdateableContext add(String key, Object value);
}
