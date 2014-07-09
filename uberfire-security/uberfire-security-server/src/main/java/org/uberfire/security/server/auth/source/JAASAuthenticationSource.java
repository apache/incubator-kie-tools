package org.uberfire.security.server.auth.source;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.auth.RolesMode;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.auth.source.adapter.RolesAdapter;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.security.server.SecurityConstants.*;

public class JAASAuthenticationSource implements AuthenticationSource,
                                                 RoleProvider {

    public static final String DEFAULT_ROLE_PRINCIPLE_NAME = "Roles";
    private String rolePrincipleName = DEFAULT_ROLE_PRINCIPLE_NAME;
    private final ThreadLocal<Subject> subjects = new ThreadLocal<Subject>();

    private String domain = "ApplicationRealm";

    private RolesMode mode = RolesMode.ROLE;

    private ServiceLoader<RolesAdapter> rolesAdapterServiceLoader = ServiceLoader.load( RolesAdapter.class );

    @Override
    public void initialize( final Map<String, ?> options ) {
        if ( options.containsKey( AUTH_DOMAIN_KEY ) ) {
            domain = (String) options.get( AUTH_DOMAIN_KEY );
        }
        if ( options.containsKey( ROLES_IN_CONTEXT_KEY ) ) {
            rolePrincipleName = (String) options.get( ROLES_IN_CONTEXT_KEY );
        }
        try {
            if ( options.containsKey( ROLE_MODE_KEY ) ) {
                mode = RolesMode.valueOf( (String) options.get( ROLE_MODE_KEY ) );
            }
        } catch ( final Exception ignore ) {
            mode = RolesMode.GROUP;
        }
    }

    @Override
    public boolean supportsCredential( final Credential credential ) {
        return credential != null && credential instanceof UsernamePasswordCredential;
    }

    @Override
    public boolean authenticate( final Credential credential,
                                 final SecurityContext securityContext ) {
        final UsernamePasswordCredential usernamePasswd = checkInstanceOf( "credential", credential, UsernamePasswordCredential.class );

        try {
            final LoginContext loginContext = new LoginContext( domain, new UsernamePasswordCallbackHandler( usernamePasswd ) );
            loginContext.login();
            subjects.set( loginContext.getSubject() );

            return true;
        } catch ( final Exception ignored ) {
        }

        return false;
    }

    @Override
    public List<Role> loadRoles( final Principal principal,
                                 final SecurityContext securityContext ) {
        final List<Role> roles = new ArrayList<Role>();
        try {
            final Subject subject = subjects.get();

            if ( subject != null ) {
                final Set<java.security.Principal> principals = subject.getPrincipals();

                if ( principals != null ) {
                    for ( java.security.Principal p : principals ) {
                        if ( p instanceof Group && rolePrincipleName.equalsIgnoreCase( p.getName() ) ) {
                            final Enumeration<? extends java.security.Principal> groups = ( (Group) p ).members();
                            while ( groups.hasMoreElements() ) {
                                final java.security.Principal groupPrincipal = groups.nextElement();
                                roles.add( new RoleImpl( groupPrincipal.getName() ) );
                            }
                            break;
                        }
                    }
                }

                if ( rolesAdapterServiceLoader != null && rolesAdapterServiceLoader.iterator().hasNext() ) {
                    for ( final RolesAdapter rolesAdapter : rolesAdapterServiceLoader ) {
                        rolesAdapter.getRoles( principal, securityContext, mode );
                    }
                }
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return roles;
    }

    class UsernamePasswordCallbackHandler implements CallbackHandler {

        private final UsernamePasswordCredential credential;

        public UsernamePasswordCallbackHandler( final UsernamePasswordCredential credential ) {
            this.credential = credential;
        }

        @Override
        public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
            for ( final Callback callback : callbacks ) {
                if ( callback instanceof NameCallback ) {
                    NameCallback nameCB = (NameCallback) callback;
                    nameCB.setName( credential.getUserName() );
                } else if ( callback instanceof PasswordCallback ) {
                    PasswordCallback passwordCB = (PasswordCallback) callback;
                    passwordCB.setPassword( credential.getPassword().toString().toCharArray() );
                } else {
                    try {
                        final Method method = callback.getClass().getMethod( "setObject", Object.class );
                        method.invoke( callback, credential.getPassword().toString() );
                    } catch ( final Exception ignored ) {
                    }
                }
            }
        }
    }

}
