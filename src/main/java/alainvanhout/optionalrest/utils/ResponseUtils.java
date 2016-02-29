package alainvanhout.optionalrest.utils;

import alainvanhout.optionalrest.RestResponse;
import alainvanhout.optionalrest.request.RestRequest;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

public class ResponseUtils {

    public static ResponseEntity toResponseEntity(RestResponse response) {
        return new ResponseEntity(response.render().getBytes(), HttpStatus.valueOf(response.getResponseCode()));
    }

}
