package demo.root;

import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.response.StreamResponse;
import optionalrest.core.scope.definition.ScopeContainer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class AddressProxyScope implements ScopeContainer {

    @Get
    public Response arriveProxy(Request request) throws IOException {
        String url = "http://localhost:8080/persons/2/address";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        for (Map.Entry<String, List<String>> header : request.getHeaders().toMap().entrySet()) {
            connection.setRequestProperty(header.getKey(), StringUtils.join(header.getValue(), ";"));
        }

        connection.setRequestMethod(request.getMethod().toString());
        Response response = new StreamResponse(connection.getInputStream()).responseCode(connection.getResponseCode());
        response.getHeaders().addAll(connection.getHeaderFields());
        return response;
    }
}
