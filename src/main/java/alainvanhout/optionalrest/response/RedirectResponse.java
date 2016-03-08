package alainvanhout.optionalrest.response;

import java.io.InputStream;

public class RedirectResponse extends BasicResponse {

    @Override
    public InputStream toStream() {
        return null;
    }

    public RedirectResponse url(String redirectUrl) {
        getHeaders().add("Location", redirectUrl);
        responseCode(ResponseCode.FOUND);
        return this;
    }
}
