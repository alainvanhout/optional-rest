package alainvanhout.rest.services.factories;

import alainvanhout.rest.scope.ScopeContainer;
import org.apache.commons.lang3.StringUtils;

public class ScopeFactoryUtils {


    public static final String INSTANCE = "instance";

    public static String determineParentName(String parentName, ScopeContainer container) {
        if (StringUtils.isBlank(parentName)) {
            return container.getClass().getCanonicalName();
        }
        return parentName;
    }

    public static String determineRelativeName(String relativeName, String relative, String parentName) {
        if (StringUtils.isBlank(relativeName)) {
            return parentName + ":" + relative;
        }
        return relativeName;
    }

    public static String determineInstanceName(String name, String parentName) {
        if (StringUtils.isBlank(name)) {
            return parentName + ":" + INSTANCE;
        }
        return name;
    }
}
