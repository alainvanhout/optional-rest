package alainvanhout.optionalrest.response;

import alainvanhout.optionalrest.request.Headers;

import java.io.InputStream;

public interface Response {
    int getResponseCode();

    Response responseCode(int responseCode);

    String getRedirectUrl();

    Response redirectUrl(String redirectUrl);

    Headers getHeaders();

    InputStream toStream();
}
