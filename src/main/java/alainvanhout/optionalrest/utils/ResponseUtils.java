package alainvanhout.optionalrest.utils;

import alainvanhout.optionalrest.response.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ResponseUtils {

    public static ResponseEntity toResponseEntity(Response response) {
        if (response.getRedirectUrl() != null){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", response.getRedirectUrl());
            return new ResponseEntity(null, headers, HttpStatus.FOUND);
        }
        try {
            byte[] bytes = IOUtils.toByteArray(response.toStream());
            return new ResponseEntity(bytes, HttpStatus.valueOf(response.getResponseCode()));
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
