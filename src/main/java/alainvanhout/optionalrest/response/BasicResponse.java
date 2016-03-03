package alainvanhout.optionalrest.response;

import alainvanhout.optionalrest.RestException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BasicResponse implements Response {
    private int responseCode = ResponseCode.FOUND;
    private String redirectUrl;

    @Override
    public int getResponseCode() {
        return this.responseCode;
    }

    @Override
    public Response responseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public Response redirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    @Override
    public InputStream toStream() {
        throw new UnsupportedOperationException();
    }
}
