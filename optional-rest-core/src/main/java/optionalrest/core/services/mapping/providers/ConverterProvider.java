package optionalrest.core.services.mapping.providers;

import java.util.HashMap;
import java.util.Map;

public interface ConverterProvider<Target, Converter> {

    default Map<Evaluator<Target>, Converter> getConverters(){
        Map<Evaluator<Target>, Converter> combinedConverters = new HashMap<>();

        // the ones that are directlt defined
        Map<Evaluator<Target>, Converter> converters = defineConverters();
        combinedConverters.putAll(converters);

        // the ones that map to a class
        Map<Class, Converter> mappersForClass = defineConvertersForClass();
        for (Map.Entry<Class, Converter> entry : mappersForClass.entrySet()) {
            combinedConverters.put(p -> checkClass(entry, p), entry.getValue());
        }

        return combinedConverters;
    }

    default boolean checkClass(Map.Entry<Class, Converter> entry, Target p){
        return p.getClass().equals(entry.getKey());
    }

    default Map<Evaluator<Target>, Converter> defineConverters(){
        return new HashMap<>();
    }

    default Map<Class, Converter> defineConvertersForClass(){
        return new HashMap<>();
    }
}
