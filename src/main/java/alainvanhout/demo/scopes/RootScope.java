package alainvanhout.demo.scopes;

import renderering.core.basic.StringRenderer;
import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.annotations.scopes.Relative;
import optionalrest.core.annotations.scopes.Scope;
import optionalrest.core.request.Request;
import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.request.meta.Mime;
import optionalrest.rendering.RendererResponse;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.RootScopeContainer;
import optionalrest.core.services.factories.Header;
import optionalrest.core.services.factories.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RootScope implements RootScopeContainer {

    @Relative(path = "persons")
    private PersonScope personScope;

    @Relative(path = "person")
    private PersonScope personScopeAlternate;

    @Relative(path = "addresses")
    private AddressScope addressScope;

    @Relative(path = "templates")
    private TemplateScope templateScope;

    @Relative(path = "scopes")
    @Handle(contentType = Mime.TEXT_HTML, accept = {Mime.APPLICATION_JSON, Mime.TEXT_HTML})
    private ScopeScope scopeScope;

    @Get @Handle(contentType = Mime.TEXT_HTML, accept = {Mime.APPLICATION_JSON, Mime.TEXT_ALL})
    private Response arrive(Request request) {
        return new RendererResponse().renderer(new StringRenderer("Root"));
    }

    @Handle
    private void pass(Request request,
                      @Param("-method") List<String> method,
                      @Param("-accept") List<String> accept
    ) {
        if (method != null && method.size() == 1) {
            request.method(HttpMethod.valueOf(method.get(0)));
        }
        if (accept != null) {
            request.getHeaders().clear("accept").add( "accept", accept);
        }
    }

    @Relative(path = "foo")
    @Scope("foobar")
    private String foo(){
        return "Hello foo";
    }
}
