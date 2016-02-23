package alainvanhout.rest.scope;

import alainvanhout.rest.utils.RestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScopeDefinition {
    private String name;
    private String type;
    private String description;
    private Class internalClass;
    private Map<String, ScopeDefinition> relativeDefinitions = new LinkedHashMap<>();
    private Map<String, Object> internalMap = new LinkedHashMap<>();
    private Map<String, Object> relativeMap = new LinkedHashMap<>();
    private Map<String, Object> fallbackMap = new LinkedHashMap<>();

    private ScopeDefinition formInternalMap() {

        internalMap = new LinkedHashMap<>();

        Field[] fields = internalClass.getDeclaredFields();
        for (Field field : fields) {
            String type = RestUtils.typeOfField(field);
            if (type != null) {
                internalMap.put(field.getName(), type);
            }
        }

        return this;
    }

    public ScopeDefinition formFallbackMap() {

        for (Map.Entry<String, ScopeDefinition> relative : relativeDefinitions.entrySet()) {
            ScopeDefinition scopeDefinition = relative.getValue();
            fallbackMap.put(relative.getKey(), scopeDefinition.getMap());
        }

        return this;
    }

//        if (StringUtils.isNotBlank(type)) {
//            definitionMap.put("type", type);
//        }

//        if (fallbackScope != null && StringUtils.isNotBlank(fallbackScope.getType())){
//            definitionMap.put(fallbackScope.getType(), fallbackScope.getDefinitionMap());
//        }

    public Map<String, Object> getInternalMap() {
        return internalMap;
    }

    public Map<String, Object> getRelativeMap() {
        return relativeMap;
    }

    public String getDescription() {
        return description;
    }

    public ScopeDefinition description(String description) {
        this.description = description;
        return this;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(type)) {
            map.put("name", name);
        }
        if (StringUtils.isNotBlank(type)) {
            map.put("type", type);
        }
        if (internalMap.size() > 0) {
            map.put("internal", internalMap);
        }
        if (fallbackMap.size() > 0) {
            map.putAll(fallbackMap);
        }
        if (relativeMap.size() > 0) {
            map.put("relative", relativeMap);
        }
        return map;
    }

    public ScopeDefinition internalClass(Class internalClass) {
        if (!Void.class.equals(internalClass)){
            this.internalClass = internalClass;
            formInternalMap();
        }
        return this;
    }
    public String getType() {
        return type;
    }

    public ScopeDefinition type(String type) {
        this.type = type;
        return this;
    }

    public ScopeDefinition name(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public ScopeDefinition addFallback(String fallback, ScopeDefinition definition){
        relativeDefinitions.put(fallback, definition);
        formFallbackMap();
        return this;
    }

    public Class getInternalClass() {
        return internalClass;
    }

    public void setInternalClass(Class internalClass) {
        this.internalClass = internalClass;
    }

    @Override
    public String toString() {
        return ObjectUtils.firstNonNull(name, internalClass).toString();
    }
}
