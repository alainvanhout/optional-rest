package alainvanhout.demo.scopes;

import optionalrest.core.annotations.requests.methods.Get;
import optionalrest.core.request.Request;
import optionalrest.core.scope.definition.ScopeContainer;
import org.springframework.stereotype.Service;
import renderering.core.Renderer;
import renderering.core.model.JsonRenderer;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsScope implements ScopeContainer {

    private Map<String, Integer> counts = new HashMap<>();

    @Get
    private void before(Request request){
        if (!request.getPath().isArrived()){
            String query = request.getQuery();
            if (!counts.containsKey(query)){
                counts.put(query, 0);
            }
            counts.put(query, counts.get(query) + 1);
        }
    }

    @Get
    private Renderer arrive(){
        return new JsonRenderer(counts);
    }
}
