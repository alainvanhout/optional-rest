package optionalrest.core.services.mapping.providers.parameters;

import optionalrest.core.services.mapping.providers.ConverterProvider;

import java.lang.reflect.Parameter;
import java.util.Map;

public interface ParameterConverterProvider extends ConverterProvider<Parameter, ParameterConverter> {

    @Override
    default boolean checkClass(Map.Entry<Class, ParameterConverter> entry, Parameter p){
        return  p.getType().equals(entry.getKey());
    }
}
