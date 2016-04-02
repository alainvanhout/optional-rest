package optionalrest.spring;

import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.ScopeHelper;
import optionalrest.core.services.ScopeManager;
import optionalrest.core.services.ScopeRegistry;
import optionalrest.core.services.mapping.providers.BasicParameterMapperProvider;
import optionalrest.core.services.mapping.providers.ParameterMapperProvider;
import optionalrest.core.services.mapping.providers.ResponseConverterProvider;
import optionalrest.rendering.BasicResponseConverterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Collection;

@Configuration
@ComponentScan
public class OptionalRestConfiguration {

    @Bean
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setDispatchOptionsRequest(true);
        return servlet;
    }

    @Bean
    public ParameterMapperProvider basicMapperProvider(){
        return new BasicParameterMapperProvider();
    }

    @Bean
    public ResponseConverterProvider basicResponseConverterProvider(){
        return new BasicResponseConverterProvider();
    }

    @Autowired
    private Collection<ResponseConverterProvider> responseConverterProviders;

    @Autowired
    private Collection<ParameterMapperProvider> parameterMapperProviders;

    @Autowired
    public Collection<ScopeContainer> containers;

    @Bean
    public ScopeRegistry scopeRegistry() {
        return new ScopeRegistry();
    }

    private ScopeHelper scopeHelper() {
        return new ScopeHelper();
    }

    @Bean
    public ScopeManager scopeManager(){

        ScopeManager scopeManager = new ScopeManager();

        scopeManager.scopeHelper(scopeHelper())
                .scopeRegistry(scopeRegistry())
                .containers(containers)
                .parameterMapperProviders(parameterMapperProviders)
                .responseConverterProviders(responseConverterProviders);

        scopeManager.initialize();

        return scopeManager;
    }
}
