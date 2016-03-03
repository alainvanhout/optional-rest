package alainvanhout.optionalrest.services;

import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.response.Response;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.factories.ResourceScopeFactory;
import alainvanhout.optionalrest.services.factories.ScopeFactory;
import alainvanhout.optionalrest.services.mapping.providers.ParameterMapperProvider;
import alainvanhout.optionalrest.services.mapping.providers.ResponseConverterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class ScopeManager {

    @Autowired
    private ScopeRegistry scopeRegistry;

    @Autowired
    private ResourceScopeFactory scopeFactory;

    @Autowired
    private Collection<ScopeContainer> containers;

    @Autowired
    private Collection<ScopeFactory> factories;

    @Autowired
    private Collection<ParameterMapperProvider> parameterMapperProviders;

    @Autowired
    private Collection<ResponseConverterProvider> responseConverterProviders;

    private Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> parameterMappers = new HashMap<>();

    private Map<Class, Function<Object, Object>> responseTypeMappers = new HashMap<>();

    @PostConstruct
    public void setup() {
        for (ParameterMapperProvider parameterMapperProvider : parameterMapperProviders) {
            parameterMappers.putAll(parameterMapperProvider.getCombinedParameterMappers());
        }

        for (ResponseConverterProvider responseConverterProvider : responseConverterProviders) {
            responseTypeMappers.putAll(responseConverterProvider.getResponseConverters());
        }

        for (ScopeFactory factory : factories) {
            for (ScopeContainer scopeContainer : containers) {
                factory.processContainer(scopeContainer, parameterMappers, responseTypeMappers);
            }
        }
    }

    public Response follow(ScopeContainer container, Request request) {
        Response follow = scopeRegistry.findByContainer(container).follow(request);
        return follow;
    }
}
