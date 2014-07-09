package org.uberfire.security.server.auth.source;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.acl.Group;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.WebRoleRefPermission;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.auth.RolesMode;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.server.auth.source.adapter.RolesAdapter;

import static java.util.Collections.*;
import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.security.server.SecurityConstants.*;

public class JACCAuthenticationSource implements AuthenticationSource,
                                                 RoleProvider {

    public static final String DEFAULT_ROLE_PRINCIPLE_NAME = "Roles";
    private String rolePrincipleName = DEFAULT_ROLE_PRINCIPLE_NAME;

    private ServiceLoader<RolesAdapter> rolesAdapterServiceLoader = ServiceLoader.load( RolesAdapter.class );

    private RolesMode mode = RolesMode.GROUP;

    @Override
    public void initialize( final Map<String, ?> options ) {
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
        return credential != null && credential instanceof UserNameCredential;
    }

    @Override
    public boolean authenticate( final Credential credential,
                                 final SecurityContext securityContext ) {
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
        } catch ( final Exception ignored ) {
        }
        return false;
    }

    @Override
    public List<Role> loadRoles( final Principal principal,
                                 final SecurityContext securityContext ) {

        final List<Role> roles = new ArrayList<Role>();
        try {
            Subject subject = getSubjectFromContainer();

            if ( subject != null ) {

                if ( mode.equals( RolesMode.ROLE ) || mode.equals( RolesMode.BOTH ) ) {
                    roles.addAll( loadRoles( subject ) );
                }

                if ( mode.equals( RolesMode.GROUP ) || mode.equals( RolesMode.BOTH ) ) {
                    roles.addAll( loadGroups( subject ) );
                }

            } else {
                // use adapters
                for ( final RolesAdapter adapter : rolesAdapterServiceLoader ) {
                    final List<Role> userRoles = adapter.getRoles( principal, securityContext, mode );
                    if ( userRoles != null ) {
                        roles.addAll( userRoles );
                    }
                }
            }
        } catch ( final Exception ignored ) {
        }
        return roles;
    }

    private List<Role> loadGroups( Subject subject ) {
        final List<Role> roles = new ArrayList<Role>();

        final Set<java.security.Principal> principals = subject.getPrincipals();

        if ( principals != null ) {

            for ( java.security.Principal p : principals ) {
                if ( p instanceof Group && rolePrincipleName.equalsIgnoreCase( p.getName() ) ) {
                    Enumeration<? extends java.security.Principal> groups = ( (Group) p ).members();

                    while ( groups.hasMoreElements() ) {
                        final java.security.Principal groupPrincipal = groups.nextElement();
                        roles.add( new RoleImpl( groupPrincipal.getName() ) );
                    }
                    break;
                }
            }
        }

        return roles;
    }

    private List<Role> loadRoles( final Subject subject ) {
        final List<Role> roles = new ArrayList<Role>();

        final PermissionCollection permissionCollection = Policy.getPolicy().getPermissions(
                new ProtectionDomain(
                        new CodeSource( null, (Certificate[]) null ),
                        null,
                        null,
                        subject.getPrincipals().toArray( new java.security.Principal[ subject.getPrincipals().size() ] ) ) );

        permissionCollection.implies( new WebRoleRefPermission( "", "nothing" ) );

        for ( final Permission permission : list( permissionCollection.elements() ) ) {
            if ( permission instanceof WebRoleRefPermission ) {
                final String role = permission.getActions();
                final Role roleImpl = new RoleImpl( role );
                if ( !roles.contains( roleImpl ) ) {
                    roles.add( roleImpl );
                }
            }
        }

        return roles;
    }

    protected Subject getSubjectFromContainer() {
        try {
            return (Subject) PolicyContext.getContext( "javax.security.auth.Subject.container" );
        } catch ( Exception e ) {
            return null;
        }
    }
}
