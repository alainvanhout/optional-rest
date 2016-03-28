package optionalrest.spring;

import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.RootScopeContainer;
import optionalrest.core.services.ScopeManager;
import optionalrest.spring.utils.RequestUtils;
import optionalrest.spring.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class RootController {

    @Autowired
    private RootScopeContainer rootScopeContainer;

    @Autowired
    private ScopeManager scopeManager;

    @RequestMapping(value = "/root/**",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
            produces = {"*/*"}
    )
    public ResponseEntity root(HttpServletRequest httpRequest) {
        Request request = RequestUtils.toRequest(httpRequest);
        Response response = scopeManager.follow(rootScopeContainer, request);
        return ResponseUtils.toResponseEntity(response);
    }
}
