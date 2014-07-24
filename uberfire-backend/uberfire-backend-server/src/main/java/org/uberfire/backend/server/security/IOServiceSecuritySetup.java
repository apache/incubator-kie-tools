package org.uberfire.backend.server.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.SecurityAware;
import org.uberfire.java.nio.security.Session;
import org.uberfire.java.nio.security.Subject;
import org.uberfire.java.nio.security.UserPassAuthenticator;
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
        AuthenticationService _authenticationManager = null;
        AuthorizationManager _authorizationManager = null;

        if ( authenticationManagers.isUnsatisfied() ) {
            final String authType = System.getProperty( "org.uberfire.io.auth", null );
            final String domain = System.getProperty( AUTH_DOMAIN_KEY, JAASAuthenticationService.DEFAULT_DOMAIN );

            if ( authType == null || authType.toLowerCase().equals( "jaas" ) || authType.toLowerCase().equals( "container" ) ) {
                _authenticationManager = new JAASAuthenticationService( domain );
            } else {
                _authenticationManager = loadClazz( authType, AuthenticationService.class );
            }
        }

        if ( authorizationManagers.isUnsatisfied() ) {
            _authorizationManager = new FileSystemAuthorizationManager();
        }

        final AuthorizationManager authorizationManager = _authorizationManager;
        final AuthenticationService authenticationManager = _authenticationManager;

        final org.uberfire.java.nio.security.AuthorizationManager ioAuthorizationManager = new org.uberfire.java.nio.security.AuthorizationManager() {
            @Override
            public boolean authorize( final FileSystem fs,
                                      final Subject subject ) {
                return authorizationManager.authorize( new FileSystemResourceAdaptor( fs ), ( (UserSubjectAdapter) subject ).getWrappedUser() );
            }
        };

        for ( final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders() ) {
            if ( fileSystemProvider instanceof SecurityAware ) {
                ( (SecurityAware) fileSystemProvider ).setUserPassAuthenticator( new UserPassAuthenticator() {
                    @Override
                    public boolean authenticate( String username,
                                                 String password,
                                                 Session session ) {
                        try {
                            final User result = authenticationManager.login( username, password );
                            if ( result != null ) {
                                session.setSubject( new UserSubjectAdapter( result ) );
                            }
                            return result != null;
                        } catch ( final Exception ignored ) {
                        }
                        return false;
                    }
                } );
                ( (SecurityAware) fileSystemProvider ).setAuthorizationManager( ioAuthorizationManager );
            }
        }
    }

    static class UserSubjectAdapter implements Subject {

        private final User user;

        UserSubjectAdapter( User user ) {
            this.user = user;
        }

        @Override
        public String getName() {
            return user.getIdentifier();
        }

        public User getWrappedUser() {
            return user;
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
