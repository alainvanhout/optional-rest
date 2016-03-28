package alainvanhout.demo.scopes;

import alainvanhout.demo.entities.Address;
import alainvanhout.demo.entities.Person;
import optionalrest.core.RestException;
import optionalrest.core.annotations.EntityDefinition;
import optionalrest.core.annotations.Error;
import optionalrest.core.annotations.ScopeDefinition;
import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.request.Request;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.utils.JsonUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import renderering.core.Renderer;
import renderering.core.model.JsonRenderer;
import renderering.core.model.XmlRenderer;
import renderering.web.html.basic.documentbody.PreRenderer;
import org.springframework.stereotype.Service;

@Service
@ScopeDefinition(name = "address")
@EntityDefinition(instanceClass = Address.class)
public class AddressScope implements ScopeContainer {

    @Get
    public Renderer arrive(Request request) {
        Person person = request.getContext().get("person");
        return new PreRenderer(StringEscapeUtils.escapeHtml4(new XmlRenderer(person.getAddress()).render()));
    }

    @Error
    public String error(RestException exception) {
        return "An address error has occurred > " + exception.getMessage();
    }
}
