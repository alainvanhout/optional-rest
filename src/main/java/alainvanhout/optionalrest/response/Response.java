package alainvanhout.optionalrest.response;

import alainvanhout.optionalrest.request.Headers;

import java.io.InputStream;

public interface Response {
    int getResponseCode();

    Response responseCode(int responseCode);

    Headers getHeaders();

    InputStream toStream();
}
