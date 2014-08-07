package org.uberfire.backend.server.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.security.shared.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.SecurityAware;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
@Startup
public class IOServiceSecuritySetup {

    private static final Logger LOG = LoggerFactory.getLogger( IOServiceSecuritySetup.class );

    /**
     * The system property that specifies which authentication domain the default security service should be configured
     * for. Not used if the application provides its own {@code @IOSecurityAuth AuthenticationService}.
     */
    public static final String AUTH_DOMAIN_KEY = "org.uberfire.domain";

    @Inject
    @IOSecurityAuth
    @Any
    private Instance<AuthenticationService> authenticationManagers;

    @Inject
    @IOSecurityAuthz
    @Any
    private Instance<AuthorizationManager> authorizationManagers;

    @PostConstruct
    public void setup() {
        AuthenticationService authenticationManager = null;
        AuthorizationManager authorizationManager = null;

        if ( authenticationManagers.isUnsatisfied() ) {
            final String authType = System.getProperty( "org.uberfire.io.auth", null );
            final String domain = System.getProperty( AUTH_DOMAIN_KEY, JAASAuthenticationService.DEFAULT_DOMAIN );

            if ( authType == null || authType.toLowerCase().equals( "jaas" ) || authType.toLowerCase().equals( "container" ) ) {
                authenticationManager = new JAASAuthenticationService( domain );
            } else {
                authenticationManager = loadClazz( authType, AuthenticationService.class );
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

    private <T> T loadClazz( final String clazzName,
                             final Class<T> typeOf ) {

        if ( clazzName == null || clazzName.isEmpty() ) {
            return null;
        }

        try {
            final Class<?> clazz = Class.forName( clazzName );

            if ( !typeOf.isAssignableFrom( clazz ) ) {
                // FIXME this could only be due to a deployment configuration error. why do we continue in this case?
                LOG.error( "Class '" + clazzName + "' is not assignable to expected type " + typeOf + ". Continuing as if no class was specified." );
                return null;
            }

            return typeOf.cast( clazz.newInstance() );
        } catch ( final Exception e ) {
            // FIXME this could only be due to a deployment error. why do we continue in this case?
            LOG.error( "Failed to load class '" + clazzName + "' as type " + typeOf + ". Continuing as if none was specified.",
                       e );
        }

        return null;
    }

}
