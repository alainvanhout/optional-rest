package optionalrest.core.services;

import optionalrest.core.scope.GenericScope;
import optionalrest.core.scope.Scope;
import optionalrest.core.scope.definition.ScopeContainer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScopeRegistry {

    public static final String RESOURCE = "resource";
    Map<String, Scope> scopeIdMap = new LinkedHashMap<>();
    Map<Class, Scope> scopeContainerMap = new LinkedHashMap<>();

    public void add(String scopeId, Scope scope) {
        scopeIdMap.put(scopeId, scope);
    }

    public void add(ScopeContainer container, Scope scope) {
        scopeContainerMap.put(container.getClass(), scope);
    }

    public void add(Class containerClass, Scope scope) {
        add(containerClass.getCanonicalName(), scope);
    }

    public Scope findByName(String scopeId) {
        if (scopeIdMap.containsKey(scopeId)) {
            return scopeIdMap.get(scopeId);
        }
        return null;
    }

    public Scope findByContainer(ScopeContainer container) {
        return findByContainerClass(container.getClass());
    }

    public Scope findByContainerClass(Class containerClass) {
        return findByName(containerClass.getName());
    }

    public Scope produceScope(String scopeId, ScopeContainer container) {
        return produceScope(scopeId, container, RESOURCE);
    }

    public Scope produceScope(String scopeId, ScopeContainer container, String scopeType) {
        Scope scope = findByName(scopeId);
        if (scope == null) {
            scope = new GenericScope().scopeId(scopeId);
            scope.getDefinition().type(scopeType);
            add(scopeId, scope);
            if (container != null) {
                add(container, scope);
            }
        }
        return scope;
    }

    public Collection<Scope> findAll() {
        return scopeIdMap.values();
    }
}
