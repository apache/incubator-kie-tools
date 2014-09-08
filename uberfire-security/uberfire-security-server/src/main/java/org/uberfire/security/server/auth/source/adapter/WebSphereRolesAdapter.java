package org.uberfire.security.server.auth.source.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RolesMode;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.server.HttpSecurityContext;
import org.uberfire.security.server.RolesRegistry;

@ApplicationScoped
public class WebSphereRolesAdapter implements RolesAdapter {

    @Inject
    private WebSphereRoleProviderServices roleProviderServices;

    private static final Logger logger = LoggerFactory.getLogger( WebSphereRolesAdapter.class );
    private Object registry;

    public WebSphereRolesAdapter() {
        try {
            this.registry = InitialContext.doLookup( "UserRegistry" );
        } catch ( NamingException e ) {
            logger.info( "Unable to look up UserRegistry in JNDI under key 'UserRegistry', disabling websphere adapter" );
        }
    }

    @Override
    public List<Role> getRoles( final Principal principal,
                                final SecurityContext securityContext,
                                final RolesMode mode ) {
        List<Role> roles = new ArrayList<Role>();
        if ( registry == null ) {
            return roles;
        }

        if ( mode.equals( RolesMode.GROUP ) || mode.equals( RolesMode.BOTH ) ) {
            try {
                Method method = registry.getClass().getMethod( "getGroupsForUser", new Class[]{ String.class } );
                List rolesIn = (List) method.invoke( registry, new Object[]{ principal.getName() } );
                if ( rolesIn != null ) {
                    for ( Object o : rolesIn ) {
                        roles.add( new RoleImpl( o.toString() ) );
                    }
                }
            } catch ( Exception e ) {
                logger.error( "Unable to get groups from registry due to {}", e.getMessage(), e );
            }
        }

        if ( mode.equals( RolesMode.ROLE ) || mode.equals( RolesMode.BOTH ) ) {
            if ( securityContext instanceof HttpSecurityContext ) {
                final HttpServletRequest request = ( (HttpSecurityContext) securityContext ).getRequest();
                for ( final Role enforcementRole : RolesRegistry.get().getRegisteredRoles() ) {
                    if ( request.isUserInRole( enforcementRole.getName() ) ) {
                        roles.add( new RoleImpl( enforcementRole.getName() ) );
                    }
                }
            } else {
                if ( roleProviderServices != null ) {
                    roles.addAll( roleProviderServices.getRoles() );
                }
            }
        }

        return roles;
    }
}
