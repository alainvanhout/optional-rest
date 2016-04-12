package demo.root;

import demo.addresses.AddressScope;
import demo.persons.PersonScope;
import optionalrest.core.annotations.Order;
import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.annotations.scopes.Relative;
import optionalrest.core.annotations.scopes.Scope;
import optionalrest.core.request.Request;
import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.request.meta.Mime;
import optionalrest.core.response.Response;
import optionalrest.core.scope.definition.RootScopeContainer;
import optionalrest.core.services.factories.Param;
import optionalrest.rendering.RendererResponse;
import org.springframework.stereotype.Service;
import renderering.core.basic.StringRenderer;

@Service
public class RootScope implements RootScopeContainer {

    @Relative(path = "persons")
    private PersonScope personScope;

    @Relative(path = "person")
    private PersonScope personScopeAlternate;

    @Relative(path = "addresses")
    private AddressScope addressScope;

    @Relative(path = "scopes")
    @Handle(contentType = Mime.TEXT_HTML, accept = {Mime.APPLICATION_JSON, Mime.TEXT_HTML})
    private ScopeScope scopeScope;

    @Relative(path = "secure")
    private SecurityScope securityScope;

    @Relative(path = "stats")
    private StatisticsScope statisticsScope;

    @Get @Handle(contentType = Mime.TEXT_HTML, accept = {Mime.APPLICATION_JSON, Mime.TEXT_ALL})
    private Response arrive(Request request) {
        return new RendererResponse().renderer(new StringRenderer("Root"));
    }

    @Handle @Order(-1)
    private void before(Request request){
        System.out.println("incoming: " + request.getMethod().name() + " " + request.getQuery());
    }

    @Handle
    private void pass(Request request,
                      @Param("-method") String method,
                      @Param("-accept") String accept
    ) {
        if (method != null) {
            request.method(HttpMethod.valueOf(method));
        }
        if (accept != null) {
            request.getHeaders().clear("accept").add( "accept", accept);
        }
    }

    @Handle @Order(1)
    private void after(Request request){
        System.out.println("adjusted to: " + request.getMethod().name() + " " + request.getQuery());
    }

    @Relative(path = "foo")
    @Scope("foobar")
    private String foo(){
        return "Hello foo";
    }
}
