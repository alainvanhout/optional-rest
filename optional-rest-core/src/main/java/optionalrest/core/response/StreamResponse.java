package optionalrest.core.response;

import java.io.InputStream;

public class StreamResponse extends BasicResponse {

    private InputStream inputStream;

    public StreamResponse(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream toStream() {
        return inputStream;
    }
}
