package alainvanhout.optionalrest.request;

import java.util.*;

public class Headers  extends Parameters{
    private static final List<String> COMMA_SEPARATED = Arrays.asList("accept", "accept-encoding", "accept-language");

    @Override
    public Parameters add(String key, String value) {
        if (COMMA_SEPARATED.contains(key)) {
            String[] split = value.split(",");
            for (String headerValue : split) {
                super.add(key, headerValue);
            }
        } else {
            super.add(key, value);
        }
        return this;
    }
}
