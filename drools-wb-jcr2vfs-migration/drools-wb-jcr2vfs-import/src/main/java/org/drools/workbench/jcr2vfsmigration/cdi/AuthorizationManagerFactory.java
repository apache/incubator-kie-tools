package org.drools.workbench.jcr2vfsmigration.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

@Singleton
public class AuthorizationManagerFactory {

    @Produces
    public AuthorizationManager getRefactoringQueryService() {
        return new RuntimeAuthorizationManager();
    }
}
