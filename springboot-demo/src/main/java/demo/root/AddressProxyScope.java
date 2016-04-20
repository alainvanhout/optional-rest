package demo.root;

import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.request.Request;
import optionalrest.core.response.Response;
import optionalrest.core.scope.LoadBalancingScope;
import optionalrest.core.scope.RemoteScope;
import optionalrest.core.scope.definition.ScopeContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class AddressProxyScope implements ScopeContainer {

    private LoadBalancingScope loadbalancer = new LoadBalancingScope();

    @PostConstruct
    private void initialize(){
        loadbalancer.add(new RemoteScope("http://localhost:8080/persons/2/address"));
        loadbalancer.add(new RemoteScope("http://localhost:8080/persons/1/address"));
        loadbalancer.add(new RemoteScope("http://localhost:8080/persons/3/address"));
    }

    @Get
    public Response arriveProxy(Request request) throws IOException {
       return loadbalancer.follow(request);
    }
}
