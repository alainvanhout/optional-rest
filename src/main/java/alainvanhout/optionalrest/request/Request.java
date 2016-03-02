package alainvanhout.optionalrest.request;

import alainvanhout.optionalrest.request.meta.HttpMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Request {
    public static final String PATH_SEPARATOR = "/";
    public static final String PATH_PARAMETERS_SEPARATOR = "?";
    public static final String PARAMETER_SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";

    private Path path;
    private HttpMethod method;
    private Headers headers = new Headers();
    private Parameters parameters = new Parameters();

    private Map<String, Object> context = new HashMap<>();
    private String query = "";
    private String queryPath = "";
    private String queryParameters = "";

    public HttpMethod getMethod() {
        return method;
    }

    public Path getPath() {
        return path;
    }

    public Request path(Path path) {
        this.path = path;
        return this;
    }

    public Request method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public boolean hasParameter(String key) {
        return parameters.contains(key);
    }

    public String getParameter(String key) {
        return parameters.getValue(key);
    }

    public Parameters getParameters() {
        return parameters;
    }

    public Request parameters(Map<String, String> parameterMap) {
        parameters.add(parameterMap);
        return this;
    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public Request queryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public String getQueryPath() {
        return queryPath;
    }

    public Request queryPath(String queryPath) {
        this.queryPath = queryPath;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public Request query(String query) {
        this.query = query;
        return this;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public void addToContext(String key, Object value){
        context.put(key, value);
    }

    public <T> T getFromContext(String key) {
        return (T) context.get(key);
    }

    public boolean done(){
        return getPath().isDone();
    }

    public static Request fromQuery(String query, String root, HttpMethod method) {
        query = StringUtils.substringAfter(query, root);

        Request request = new Request()
                .query(query)
                .queryPath(StringUtils.defaultString(StringUtils.substringBefore(query, PATH_PARAMETERS_SEPARATOR)))
                .queryParameters(StringUtils.defaultString(StringUtils.substringAfter(query, PATH_PARAMETERS_SEPARATOR)));

        request
                .path(new Path().steps(parsePath(request.getQueryPath())))
                .parameters(parseParameters(request.getQueryParameters()))
                .method(method);

        return request;
    }

    private static Queue<String> parsePath(String queryPath) {
        Queue<String> steps = new LinkedList<>();
        String[] split = StringUtils.split(queryPath, PATH_SEPARATOR);
        if (StringUtils.equals(StringUtils.left(queryPath, 1), PATH_SEPARATOR)) {
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

    public Headers getHeaders() {
        return headers;
    }
}
