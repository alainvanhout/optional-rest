package alainvanhout.rest.services.factories;

import alainvanhout.rest.scope.Scope;
import alainvanhout.rest.scope.ScopeContainer;
import alainvanhout.rest.services.ScopeManager;

public interface ScopeFactory {
    void processContainer(ScopeContainer container);
}
