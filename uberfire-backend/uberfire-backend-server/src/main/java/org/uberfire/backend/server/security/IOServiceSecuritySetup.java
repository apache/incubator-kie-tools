package org.uberfire.backend.server.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.SecurityAware;
import org.uberfire.java.nio.security.Session;
import org.uberfire.java.nio.security.Subject;
import org.uberfire.java.nio.security.UserPassAuthenticator;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.auth.RolesMode;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.server.SecurityConstants;
import org.uberfire.security.server.UserPassSecurityContext;
import org.uberfire.security.server.auth.impl.JAASAuthenticationManager;
import org.uberfire.security.server.auth.impl.PropertyAuthenticationManager;

import static org.uberfire.security.server.SecurityConstants.*;

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
        AuthenticationManager _authenticationManager = null;
        AuthorizationManager _authorizationManager = null;

        if ( authenticationManagers.isUnsatisfied() ) {
            final String authType = System.getProperty( "org.uberfire.io.auth", null );
            final String domain = System.getProperty( SecurityConstants.AUTH_DOMAIN_KEY, null );
            final String _mode = System.getProperty( ROLE_MODE_KEY, RolesMode.GROUP.toString() );
            RolesMode mode;
            try {
                mode = RolesMode.valueOf( _mode );
            } catch ( final Exception ignore ) {
                mode = RolesMode.GROUP;
            }

            if ( authType == null || authType.toLowerCase().equals( "jaas" ) || authType.toLowerCase().equals( "container" ) ) {
                _authenticationManager = new JAASAuthenticationManager( domain, mode );
            } else if ( authType.toLowerCase().equals( "property" ) ) {
                _authenticationManager = new PropertyAuthenticationManager( null );
            } else {
                _authenticationManager = loadClazz( authType, AuthenticationManager.class );
            }
        }

        if ( authorizationManagers.isUnsatisfied() ) {
            _authorizationManager = new FileSystemAuthorizationManager();
        }

        final AuthorizationManager authorizationManager = _authorizationManager;
        final AuthenticationManager authenticationManager = _authenticationManager;

        final org.uberfire.java.nio.security.AuthorizationManager ioAuthorizationManager = new org.uberfire.java.nio.security.AuthorizationManager() {
            @Override
            public boolean authorize( final FileSystem fs,
                                      final Subject subject ) {
                return authorizationManager.authorize( new FileSystemResourceAdaptor( fs ), ( (SubjectWrapper) subject ).getRealSubject() );
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
                            final org.uberfire.security.Subject result = authenticationManager.authenticate( new UserPassSecurityContext( null, username, password ) );
                            if ( result != null ) {
                                session.setSubject( new SubjectWrapper( result ) );
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

    class SubjectWrapper implements Subject {

        private final org.uberfire.security.Subject realSubject;

        SubjectWrapper( org.uberfire.security.Subject realSubject ) {
            this.realSubject = realSubject;
        }

        @Override
        public String getName() {
            return realSubject.getName();
        }

        public org.uberfire.security.Subject getRealSubject() {
            return realSubject;
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
                return null;
            }

            return typeOf.cast( clazz.newInstance() );
        } catch ( final ClassNotFoundException ignored ) {
        } catch ( final InstantiationException ignored ) {
        } catch ( final IllegalAccessException ignored ) {
        }

        return null;
    }

}
