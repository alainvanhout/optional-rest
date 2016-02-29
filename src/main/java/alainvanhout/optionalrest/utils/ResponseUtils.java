package alainvanhout.optionalrest.utils;

import alainvanhout.optionalrest.response.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ResponseUtils {

    public static ResponseEntity toResponseEntity(Response response) {
        try {
            byte[] bytes = IOUtils.toByteArray(response.toStream());
            return new ResponseEntity(bytes, HttpStatus.valueOf(response.getResponseCode()));
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
