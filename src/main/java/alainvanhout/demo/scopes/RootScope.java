package alainvanhout.demo.scopes;

import alainvanhout.optionalrest.annotations.resource.RestRelative;
import alainvanhout.optionalrest.annotations.resource.RestScope;
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

    @RestRelative(path = "persons")
    private PersonScope personScope;

    @RestRelative(path = "person")
    private PersonScope personScopeAlternate;

    @RestRelative(path = "addresses")
    private AddressScope addressScope;

    @RestRelative(path = "templates")
    private TemplateScope templateScope;

    @RestRelative(path = "scopes")
    private ScopeScope scopeScope;

    @RestScope(contentType = Mime.TEXT_HTML)
    private Response arrive(Request request) {
        return new RendererResponse().renderer(new StringRenderer("Root"));
    }

    @RestScope(methods = {HttpMethod.GET, HttpMethod.POST},
            accept = Mime.APPLICATION_JSON
    )
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
}
