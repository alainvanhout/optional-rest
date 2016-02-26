package alainvanhout.optionalrest.services.mapping.providers;

import java.util.Map;
import java.util.function.Function;

public interface ResponseTypeMapperProvider {

    Map<Class, Function<Object, Object>> getResponseTypeMappers();
}
