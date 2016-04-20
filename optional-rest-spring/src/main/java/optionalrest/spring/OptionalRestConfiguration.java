package optionalrest.spring;

import optionalrest.core.scope.definition.ScopeContainer;
import optionalrest.core.services.ScopeManager;
import optionalrest.core.services.ScopeRegistry;
import optionalrest.core.services.mapping.providers.annotations.AnnotationConverterProvider;
import optionalrest.core.services.mapping.providers.parameters.BasicParameterConverterProvider;
import optionalrest.core.services.mapping.providers.parameters.ParameterConverterProvider;
import optionalrest.core.services.mapping.providers.responses.ResponseConverterProvider;
import optionalrest.rendering.BasicResponseConverterProvider;
import optionalrest.rendering.OptionsRequestHandlerImpl;
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
    public ParameterConverterProvider basicMapperProvider() {
        return new BasicParameterConverterProvider();
    }

    @Bean
    public ResponseConverterProvider basicResponseConverterProvider() {
        return new BasicResponseConverterProvider();
    }

    @Autowired
    private Collection<ResponseConverterProvider> responseConverterProviders;

    @Autowired
    private Collection<ParameterConverterProvider> parameterConverterProviders;

    @Autowired
    private Collection<AnnotationConverterProvider> annotationConverterProviders;

    @Autowired
    public Collection<ScopeContainer> containers;

    @Bean
    public ScopeRegistry scopeRegistry() {
        return new ScopeRegistry();
    }

    @Bean
    public ScopeManager scopeManager() {

        ScopeManager scopeManager = new ScopeManager();

        scopeManager.scopeRegistry(scopeRegistry())
                .containers(containers)
                .optionsRequestHandler(new OptionsRequestHandlerImpl())
                .parameterMapperProviders(parameterConverterProviders)
                .responseConverterProviders(responseConverterProviders)
                .annotationConverters(annotationConverterProviders);

        scopeManager.initialize();

        return scopeManager;
    }
}
