package alainvanhout.demo.scopes;

import alainvanhout.optionalrest.annotations.Handle;
import alainvanhout.optionalrest.annotations.Relative;
import alainvanhout.optionalrest.annotations.Scope;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.request.meta.HttpMethod;
import alainvanhout.optionalrest.request.meta.Mime;
import alainvanhout.optionalrest.response.RendererResponse;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.factories.Header;
import alainvanhout.optionalrest.services.factories.Param;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RootScope implements ScopeContainer {

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

    @Handle(contentType = Mime.TEXT_HTML, accept = {Mime.APPLICATION_JSON, Mime.TEXT_ALL})
    private Response arrive(Request request) {
        return new RendererResponse().renderer(new StringRenderer("Root"));
    }

    @Handle
    private void pass(Request request,
                      @Param("OPTIONS") List<String> options,
                      @Param("DELETE") List<String> delete,
                      @Header("accept") List<String> accept
    ) {
        if (options != null) {
            request.method(HttpMethod.OPTIONS);
        }
        if (delete != null) {
            request.method(HttpMethod.DELETE);
        }
    }

    @Relative(path = "foo")
    @Scope("foobar")
    private String foo(){
        return "Hello foo";
    }
}
