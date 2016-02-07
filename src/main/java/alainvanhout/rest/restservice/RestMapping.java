package alainvanhout.rest.restservice;

public class RestMapping {

    private Object owner;
    private String accessibleName;
    private RestMappingType type;

    public RestMapping(Object owner, String accessibleName, RestMappingType type) {
        this.owner = owner;
        this.accessibleName = accessibleName;
        this.type = type;
    }

    public String getAccessibleName() {
        return accessibleName;
    }

    public void setAccessibleName(String accessibleName) {
        this.accessibleName = accessibleName;
    }

    public RestMappingType getType() {
        return type;
    }

    public void setType(RestMappingType type) {
        this.type = type;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Class owner) {
        this.owner = owner;
    }

    public enum RestMappingType {
        FIELD,
        METHOD
    }
}
