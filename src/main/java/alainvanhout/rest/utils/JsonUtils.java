package alainvanhout.rest.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
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

    public static String entityToJson(Class entity) {
        Map<String, Map> definitionMap = getDefinitionMap(entity);
        return definitionToJson(definitionMap);
    }

    public static String definitionToJson(Map<String, Map> definitionMap) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(definitionMap);
    }

    public static Map<String, Map> getDefinitionMap(Class entity) {
        Map<String, String> primitive = new HashMap<>();
        Map<String, String> relative = new HashMap<>();
        Field[] fields = entity.getDeclaredFields();
        for (Field field : fields) {
            String type = getType(field);
            if (type != null) {
                primitive.put(field.getName(), type);
            } else {
                relative.put(field.getName(), field.getType().getSimpleName());
            }
        }

        Map<String, Map> entityMap = new HashMap<>();
        entityMap.put("primitive", primitive);
        entityMap.put("relative", relative);
        return entityMap;
    }

    public static String getType(Field field) {
        Class<?> type = field.getType();

        if (field.getType().isAssignableFrom(List.class) || field.getType().isAssignableFrom(List.class)) {
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            return getType(listClass) + "[]";
        }
        return getType(type);
    }

    public static String getType(Class<?> type) {
        if (primitiveTypes.containsKey(type)){
            return primitiveTypes.get(type);
        }

        return null;
    }
}
