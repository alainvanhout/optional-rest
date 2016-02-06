package alainvanhout.rest.restservice;

public class RestMapping {

    private String accessibleName;
    private RestMappingType type;

    public RestMapping(String accessibleName, RestMappingType type) {
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

    public enum RestMappingType {
        FIELD,
        METHOD
    }
}
