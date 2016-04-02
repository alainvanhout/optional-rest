package optionalrest.spring.utils;

import optionalrest.core.request.Headers;
import optionalrest.core.response.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;

public class ResponseUtils {

    public static ResponseEntity toResponseEntity(Response response) {
        try {
            byte[] bytes = toBytes(response.toStream());
            HttpHeaders responseHeaders = toHttpHeaders(response.getHeaders());
            return new ResponseEntity(bytes, responseHeaders, HttpStatus.valueOf(response.getResponseCode()));
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static byte[] toBytes(InputStream inputStream) throws IOException {
        byte[] bytes = null;
        if (inputStream != null) {
            bytes = IOUtils.toByteArray(inputStream);
        }
        return bytes;
    }

    private static HttpHeaders toHttpHeaders(Headers headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String key : headers.getKeys()) {
            httpHeaders.put(key, headers.get(key));
        }
        return httpHeaders;
    }
}
