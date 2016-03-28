package optionalrest.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestUtils {

    private static Map<Class, String> primitiveTypes = new HashMap<>();

    static {
        // string
        primitiveTypes.put(String.class, "string");
        //integer
        primitiveTypes.put(Integer.class, "integer");
        primitiveTypes.put(Long.class, "integer");
        primitiveTypes.put(Byte.class, "integer");
        primitiveTypes.put(BigInteger.class, "integer");
        //decimal
        primitiveTypes.put(Double.class, "decimal");
        primitiveTypes.put(Float.class, "decimal");
        primitiveTypes.put(BigDecimal.class, "decimal");
    }

    public static String typeOfField(Field field) {
        Class<?> type = field.getType();

        if (field.getType().isAssignableFrom(List.class) || field.getType().isAssignableFrom(List.class)) {
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            return typeOfField(listClass) + "[]";
        }
        return typeOfField(type);
    }

    public static String typeOfField(Class<?> type) {
        if (primitiveTypes.containsKey(type)) {
            return primitiveTypes.get(type);
        }

        return null;
    }
}
