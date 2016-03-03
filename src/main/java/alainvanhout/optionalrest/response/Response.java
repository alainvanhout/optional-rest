package alainvanhout.optionalrest.response;

import java.io.InputStream;

public interface Response {
    int getResponseCode();

    Response responseCode(int responseCode);

    String getRedirectUrl();

    Response redirectUrl(String redirectUrl);

    InputStream toStream();
}
