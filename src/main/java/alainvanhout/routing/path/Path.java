package alainvanhout.routing.path;

import alainvanhout.context.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Path {

    public static final String PATH_SEPARATOR = "/";
    public static final String PATH_PARAMETERS_SEPARATOR = "?";
    public static final String PARAMETER_SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";

    private String query = "";
    private String queryPath = "";
    private String queryParameters = "";
    private Method method = Method.GET;
    private String step;
    private Queue<String> steps = new LinkedList<String>();
    private Map<String, String> parameters = new HashMap<>();

    private RoutingContext context = new RoutingContext();

    public boolean hasNextStep() {
        return !steps.isEmpty();
    }

    public boolean done() {
        return steps.isEmpty();
    }

    public String nextStep() {
        step = steps.poll();
        return step;
    }

    public String getQueryPath() {
        return queryPath;
    }

    public void setQueryPath(String queryPath) {
        this.queryPath = queryPath;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Queue<String> getSteps() {
        return steps;
    }

    public String getStep() {
        return step;
    }

    public void setSteps(Queue<String> steps) {
        this.steps = steps;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }


    public static Path fromQuery(String query) {
        return fromQuery(query, "");
    }

    public static Path fromQuery(String query, String root) {
        Path path = new Path();
        path.setQuery(StringUtils.substringAfter(query, root));

        path.setQueryPath(StringUtils.defaultString(StringUtils.substringBefore(path.getQuery(), PATH_PARAMETERS_SEPARATOR)));
        path.setQueryParameters(StringUtils.defaultString(StringUtils.substringAfter(path.getQuery(), PATH_PARAMETERS_SEPARATOR)));

        path.setSteps(parsePath(path.getQueryPath()));
        path.setParameters(parseParameters(path.getQueryParameters()));

        return path;
    }

    private static Queue<String> parsePath(String queryPath) {
        Queue<String> steps = new LinkedList<>();
        String[] split = StringUtils.split(queryPath, PATH_SEPARATOR);
        if (StringUtils.equals(StringUtils.left(queryPath, 1), PATH_SEPARATOR)){
            steps.add(PATH_SEPARATOR);
        }

        if (split != null) {
            steps.addAll(Arrays.asList(split));
        }
        return steps;
    }

    private static Map<String, String> parseParameters(String queryParameters) {
        String[] params = StringUtils.split(queryParameters, PARAMETER_SEPARATOR);
        return Arrays.asList(params).stream().collect(Collectors.toMap(
                p -> StringUtils.substringBefore(p, KEY_VALUE_SEPARATOR),
                p -> StringUtils.contains(p, KEY_VALUE_SEPARATOR) ? StringUtils.substringAfter(p, KEY_VALUE_SEPARATOR) : null
        ));
    }

    public Path addContext(Context context){
        this.context.add(context);
        return this;
    }

    public Path addToContext(String key, String value){
        context.add(key, value);
        return this;
    }

    public RoutingContext getContext() {
        return context;
    }
}
