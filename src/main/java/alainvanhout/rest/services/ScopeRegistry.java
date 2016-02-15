package alainvanhout.rest.services;

import alainvanhout.rest.RestException;
import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ScopeRegistry {

    Set<Scope> scopes = new HashSet<>();
    Map<Class, Scope> containerMap = new HashMap<>();

    public void add(Scope scope) {
        scopes.add(scope);
    }

    public void add(ScopeContainer container, Scope scope) {
        containerMap.put(container.getClass(), scope);
        add(scope);
    }

    public Scope findByName(String scopeName) {
        // TODO: exception
        return scopes.stream()
                .filter(s -> StringUtils.equals(s.getDefinition().getName(), scopeName))
                .findFirst()
                .orElse(null);
    }

    public Scope findByContainer(ScopeContainer container) {
        return findByContainerClass(container.getClass());
    }

    public Scope findByContainerClass(Class containerClass) {
        if (!containerMap.containsKey(containerClass)) {
            throw new RestException("No scope available for container " + containerClass);
        }
        return containerMap.get(containerClass);
    }

    public Set<Scope> getScopes() {
        return scopes;
    }
}
