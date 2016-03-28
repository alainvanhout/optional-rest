package optionalrest.core.request;

import alainvanhout.context.UpdateableContext;
import alainvanhout.context.impl.MapContext;
import optionalrest.core.RestException;
import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.response.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
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
    private UpdateableContext context = new MapContext();
    private Reader reader;

    private String query = "";
    private String queryPath = "";
    private String queryParameters = "";

    private Response response = null;
    private boolean done = false;

    public Request response(Response response){
        this.response = response;
        return this;
    }

    public Response getResponse(){
        return response;
    }

    public boolean hasResponse(){
        return response != null;
    }

    public Request done(){
        return done(true);
    }

    public Request done(boolean done){
        this.done = done;
        return this;
    }


    public Request done(Response response){
        response(response);
        return done();
    }

    public boolean isDone(){
        return done;
    }


    public Reader getReader() {
        return reader;
    }

    public Request reader(Reader reader) {
        this.reader = reader;
        return this;
    }

    public String bodyAsString() {
        String body = null;
        try {
            body = IOUtils.toString(reader);
            IOUtils.closeQuietly(reader);
        } catch (IOException e) {
            throw new RestException("Encountered error while parsing request body", e);
        }
        return body;
    }

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

    public UpdateableContext getContext() {
        return context;
    }

    public static Request fromQuery(String query, String root, HttpMethod method) {
        String fullQuery = query;
        query = StringUtils.substringAfter(query, root);

        Request request = new Request()
                .query(fullQuery)
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
