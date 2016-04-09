package demo.scopes;

import demo.entities.Address;
import demo.entities.Person;
import optionalrest.core.RestException;
import optionalrest.core.annotations.EntityDefinition;
import optionalrest.core.annotations.Error;
import optionalrest.core.annotations.ScopeDefinition;
import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.annotations.requests.mime.ToHtml;
import optionalrest.core.annotations.requests.mime.ToJson;
import optionalrest.core.annotations.requests.mime.ToXml;
import optionalrest.core.request.Request;
import optionalrest.core.request.meta.Mime;
import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.rendering.JsonRenderer;
import optionalrest.rendering.XmlRenderer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;
import renderering.core.Renderer;
import renderering.web.html.basic.documentbody.PreRenderer;

@Service
@ScopeDefinition(name = "address")
@EntityDefinition(instanceClass = Address.class)
public class AddressScope implements ScopeContainer {


    @Get @ToXml @ToHtml
    public Renderer arriveHtml(Request request) {
        Address address = getAddress(request);
        XmlRenderer xmlRenderer = new XmlRenderer(address);
        if (request.getHeaders().contains("accept", Mime.TEXT_HTML)){
            return new PreRenderer(StringEscapeUtils.escapeHtml4(xmlRenderer.render()));
        } else {
            return xmlRenderer;
        }
    }

    public Address getAddress(Request request) {
        Person person = request.getContext().get("person");
        return person.getAddress();
    }

    @Get @ToJson
    public Renderer arriveJson(Request request) {
        Person person = request.getContext().get("person");
        return new PreRenderer(new JsonRenderer(person.getAddress()).render());
    }

    @Error
    public String error(RestException exception) {
        return "An address error has occurred > " + exception.getMessage();
    }
}
