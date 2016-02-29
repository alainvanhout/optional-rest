package alainvanhout.optionalrest.response;

import alainvanhout.optionalrest.RestException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BasicResponse implements Response {
    private int responseCode;

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
    public InputStream toStream() {
        throw new UnsupportedOperationException();
    }
}
