package org.uberfire.security.server.auth.source;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.naming.InitialContext;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.server.auth.source.adapter.RolesAdapter;

import static org.uberfire.commons.validation.Preconditions.checkInstanceOf;
import static org.uberfire.security.server.SecurityConstants.*;

public class JACCAuthenticationSource implements AuthenticationSource,
                                                 RoleProvider {

    public static final String DEFAULT_ROLE_PRINCIPLE_NAME = "Roles";
    private String rolePrincipleName = DEFAULT_ROLE_PRINCIPLE_NAME;

    private ServiceLoader<RolesAdapter> rolesAdapterServiceLoader = ServiceLoader.load(RolesAdapter.class);

    @Override
    public void initialize( Map<String, ?> options ) {
        if ( options.containsKey( ROLES_IN_CONTEXT_KEY ) ) {
            rolePrincipleName = (String) options.get( ROLES_IN_CONTEXT_KEY );
        }
    }

    @Override
    public boolean supportsCredential( Credential credential ) {
        if ( credential == null ) {
            return false;
        }
        return credential instanceof UserNameCredential;
    }

    @Override
    public boolean authenticate( Credential credential, final SecurityContext securityContext ) {
        final UserNameCredential userNameCredential = checkInstanceOf( "credential", credential, UserNameCredential.class );
        try {
            Subject subject = (Subject) PolicyContext.getContext( "javax.security.auth.Subject.container" );

            if ( subject != null ) {
                Set<java.security.Principal> principals = subject.getPrincipals();

                if ( principals != null ) {
                    for ( java.security.Principal p : principals ) {
                        if ( p.getName().equals( userNameCredential.getUserName() ) ) {
                            return true;
                        }
                    }
                }
            }
        } catch ( Exception e ) {
        }
        return false;
    }

    @Override
    public List<Role> loadRoles( Principal principal ) {
        List<Role> roles = new ArrayList<Role>();
        try {
            Subject subject = getSubjectFromContainer();

            if ( subject != null ) {
                Set<java.security.Principal> principals = subject.getPrincipals();

                if ( principals != null ) {

                    for ( java.security.Principal p : principals ) {
                        if ( p instanceof Group && rolePrincipleName.equalsIgnoreCase( p.getName() ) ) {
                            Enumeration<? extends java.security.Principal> groups = ( (Group) p ).members();

                            while ( groups.hasMoreElements() ) {
                                final java.security.Principal groupPrincipal = (java.security.Principal) groups.nextElement();
                                roles.add( new RoleImpl( groupPrincipal.getName() ) );

                            }
                            break;

                        }
                    }
                }
            } else {
                // use adapters
                for (RolesAdapter adapter : rolesAdapterServiceLoader) {
                    List<Role> userRoles = adapter.getRoles(principal.getName());
                    if (userRoles != null) {
                        roles.addAll(userRoles);
                    }
                }
            }
        } catch ( Exception e ) {
        }
        return roles;
    }

    protected Subject getSubjectFromContainer() {
        try {
            return (Subject) PolicyContext.getContext( "javax.security.auth.Subject.container" );
        } catch (Exception e) {
            return null;
        }
    }
}
