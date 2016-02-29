package alainvanhout.optionalrest.response;

import java.io.InputStream;

public interface Response {
    int getResponseCode();

    Response responseCode(int responseCode);

    InputStream toStream();
}
