package alainvanhout.optionalrest.services.factories;

import alainvanhout.optionalrest.RestException;
import alainvanhout.optionalrest.annotations.entity.RestEntity;
import alainvanhout.optionalrest.request.Request;
import alainvanhout.optionalrest.scope.Scope;
import alainvanhout.optionalrest.scope.definition.ScopeContainer;
import alainvanhout.optionalrest.services.ScopeRegistry;
import alainvanhout.optionalrest.services.mapping.Mapping;
import alainvanhout.optionalrest.services.mapping.MethodMapping;
import alainvanhout.optionalrest.utils.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class EntityScopeFactory implements ScopeFactory {

    public static final String ENTITY = "entity";

    @Autowired
    private ScopeRegistry scopeRegistry;

    @Override
    public void processContainer(ScopeContainer container, Map<Function<Parameter, Boolean>, BiFunction<Parameter, Request, Object>> parameterMappers, Map<Class, Function<Object, Object>> responseTypeMappers) {
        try {
            for (Method method : container.getClass().getDeclaredMethods()) {
                MethodMapping mapping = new MethodMapping(container, method);
                if (processAccessibleObject(container, method, mapping, method.getReturnType().equals(Void.TYPE))) {
                    mapping.responseTypeMappers(responseTypeMappers).parameterMappers(parameterMappers);
                }
            }

            for (Field field : container.getClass().getDeclaredFields()) {
                processAccessibleObject(container, field, null, false);
            }
        } catch (SecurityException e) {
            throw new RestException("Could not process class: " + container.getClass(), e);
        }
    }

    private boolean processAccessibleObject(ScopeContainer container, AccessibleObject accessibleObject, Mapping mapping, boolean passing) {
        RestEntity annRestEntity = ReflectionUtils.retrieveAnnotation(accessibleObject, RestEntity.class);

        // pass and arrive mapping
        if (annRestEntity != null) {
            String scopeId = ScopeFactoryUtils.determineParentName(annRestEntity.scope(), container);
            Scope scope = produceEntityScope(scopeId, container);
            mapping.getSupported()
                    .methods(annRestEntity.methods())
                    .contentType(annRestEntity.contentType())
                    .accept(annRestEntity.accept());
            if (passing) {
                scope.addPassMapping(mapping);
            } else {
                scope.addArriveMapping(mapping);
            }
            return true;
        }

        return false;
    }

    private Scope produceEntityScope(String scopeId, ScopeContainer container) {
        Scope scope = scopeRegistry.produceScope(scopeId, container);
        scope.getDefinition().type(ENTITY);
        return scope;
    }
}
