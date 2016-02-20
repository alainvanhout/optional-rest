package alainvanhout.rest.services;

import alainvanhout.rest.scope.GenericScope;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ScopeRegistry {

    public static final String RESOURCE = "resource";
    Map<String, Scope> scopeNameMap = new LinkedHashMap<>();
    Map<Class, Scope> scopeContainerMap = new LinkedHashMap<>();

    public void add(String scopeName, Scope scope) {
        scopeNameMap.put(scopeName, scope);
    }

    public void add(ScopeContainer container, Scope scope) {
        add(container.getClass(), scope);
    }

    public void add(Class containerClass, Scope scope) {
        scopeContainerMap.put(containerClass, scope);
    }

    public Scope findByName(String scopeName) {
        if (scopeNameMap.containsKey(scopeName)) {
            return scopeNameMap.get(scopeName);
        }
        return null;
    }

    public Scope findByContainer(ScopeContainer container) {
        return findByContainerClass(container.getClass());
    }

    public Scope findByContainerClass(Class containerClass) {
        return findByName(containerClass.getName());
    }

    public Scope produceScope(String scopeName, ScopeContainer container) {
        return produceScope(scopeName, container, RESOURCE);
    }

    public Scope produceScope(String scopeName, ScopeContainer container, String scopeType) {
        Scope scope = findByName(scopeName);
        if (scope == null) {
            scope = new GenericScope();
            scope.getDefinition().name(scopeName).type(scopeType);
            add(scopeName, scope);
            if (container != null) {
                add(container, scope);
            }
        }
        return scope;
    }
}
