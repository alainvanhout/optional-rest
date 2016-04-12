package demo.root;

import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.request.Request;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.factories.Param;
import optionalrest.rendering.RendererResponse;
import org.springframework.stereotype.Service;
import renderering.core.basic.StringRenderer;

import java.util.List;

@Service
public class SecurityScope implements ScopeContainer {

    @Get
    private void before(Request request, @Param("-unauthorized") String unauthorized){
        if (unauthorized != null){
            request.done(new RendererResponse()
                    .renderer(new StringRenderer("Not authorized"))
                    .responseCode(401));
        }
        System.out.println("Security check was performed for: " + request.getQuery());
    }

    @Get
    private String arrive(Request request){
        return "Security scope";
    }
}
