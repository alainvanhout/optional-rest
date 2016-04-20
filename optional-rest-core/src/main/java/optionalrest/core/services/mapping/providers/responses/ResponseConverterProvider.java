package optionalrest.core.services.mapping.providers.responses;

import optionalrest.core.services.mapping.providers.ConverterProvider;

import java.util.Map;

public interface ResponseConverterProvider extends ConverterProvider<Object, ResponseConverter> {

    @Override
    default boolean checkClass(Map.Entry<Class, ResponseConverter> entry, Object p){
        return entry.getKey().isAssignableFrom(p.getClass());
    }
}
