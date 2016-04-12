package optionalrest.cms.root;

import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.request.Request;
import optionalrest.core.scope.definition.ScopeContainer;
import org.springframework.stereotype.Service;

@Service
public class SecurityScope implements ScopeContainer {

    @Get
    private void before(Request request){
        System.out.println("Security check was performed for: " + request.getQuery());
    }

    @Get
    private String arrive(Request request){
        return "Security scope";
    }
}
