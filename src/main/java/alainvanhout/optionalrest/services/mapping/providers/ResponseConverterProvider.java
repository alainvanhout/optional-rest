package alainvanhout.optionalrest.services.mapping.providers;

import java.util.Map;
import java.util.function.Function;

public interface ResponseConverterProvider {

    Map<Class, Function<Object, Object>> getResponseConverters();
}
