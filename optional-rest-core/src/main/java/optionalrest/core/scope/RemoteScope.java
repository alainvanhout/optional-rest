package optionalrest.core.scope;

import optionalrest.core.RestException;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.response.StreamResponse;
import optionalrest.core.scope.definition.ScopeDefinition;
import optionalrest.core.services.mapping.RequestHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RemoteScope extends BasicScope {

    private String url;

    public RemoteScope(String url) {
        this.url = url;
    }

    @Override
    public void pass(Request request) {
        follow(request);
    }

    @Override
    public Response arrive(Request request) {
        return follow(request);
    }

    @Override
    public ScopeDefinition getDefinition() {
        return null;
    }

    @Override
    public Response follow(Request request) {
        HttpURLConnection connection = null;

        try {
            // TODO: request parameters and request body

            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();

            for (Map.Entry<String, List<String>> header : request.getHeaders().toMap().entrySet()) {
                connection.setRequestProperty(header.getKey(), StringUtils.join(header.getValue(), ";"));
            }
            connection.setRequestMethod(request.getMethod().toString());
            connection.getResponseCode();
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        }

        try {
            InputStream inputStream = connection.getResponseCode() < 400 ? connection.getInputStream() : connection.getErrorStream();
            Response response = new StreamResponse(inputStream).responseCode(connection.getResponseCode());
            response.getHeaders().addAll(connection.getHeaderFields());
            return response;
        } catch (IOException e) {
            throw new RestException("Encountered error while following remote scope: " + this.getScopeId(), e);
        }
    }

    @Override
    public Scope addRequestHandler(RequestHandler requestHandler) {
        return null;
    }

    @Override
    public Scope addErrorRequestHandler(RequestHandler requestHandler) {
        return null;
    }

    @Override
    public void setInstanceScope(Scope scope) {

    }

    @Override
    public GenericScope optionsRequestHandler(OptionsRequestHandler optionsRequestHandler) {
        return null;
    }

    @Override
    public Supported getSupported() {
        return null;
    }

    @Override
    public Scope getInstanceScope() {
        return null;
    }
}
