package org.uberfire.backend.server.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.SecurityAware;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.server.SecurityConstants;
import org.uberfire.security.server.auth.impl.JAASAuthenticationManager;
import org.uberfire.security.server.auth.impl.PropertyAuthenticationManager;

@ApplicationScoped
@Startup
public class IOServiceSecuritySetup {

    @Inject
    @IOSecurityAuth
    @Any
    private Instance<AuthenticationManager> authenticationManagers;

    @Inject
    @IOSecurityAuthz
    @Any
    private Instance<AuthorizationManager> authorizationManagers;

    @PostConstruct
    public void setup() {
        AuthenticationManager authenticationManager = null;
        AuthorizationManager authorizationManager = null;

        if ( authenticationManagers.isUnsatisfied() ) {
            final String authType = System.getProperty( "org.uberfire.io.auth", null );
            final String domain = System.getProperty( SecurityConstants.AUTH_DOMAIN_KEY, null );

            if ( authType == null || authType.toLowerCase().equals( "jaas" ) || authType.toLowerCase().equals( "container" ) ) {
                authenticationManager = new JAASAuthenticationManager( domain );
            } else if ( authType.toLowerCase().equals( "property" ) ) {
                authenticationManager = new PropertyAuthenticationManager( null );
            } else {
                authenticationManager = null;
            }
        }

        if ( authorizationManagers.isUnsatisfied() ) {
            authorizationManager = new FileSystemAuthorizationManager();
        }

        for ( final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders() ) {
            if ( fileSystemProvider instanceof SecurityAware ) {
                ( (SecurityAware) fileSystemProvider ).setAuthenticationManager( authenticationManager );
                ( (SecurityAware) fileSystemProvider ).setAuthorizationManager( authorizationManager );
            }
        }
    }
}
