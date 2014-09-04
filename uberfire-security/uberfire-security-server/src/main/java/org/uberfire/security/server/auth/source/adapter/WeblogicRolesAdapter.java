package org.uberfire.security.server.auth.source.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
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
public class WeblogicRolesAdapter implements RolesAdapter {

    @Inject
    private WebSphereRoleProviderServices roleProviderServices;

    private static final Logger logger = LoggerFactory.getLogger( WeblogicRolesAdapter.class );
    private Class webLogicSecurity;

    public WeblogicRolesAdapter() {
        try {
            this.webLogicSecurity = Class.forName("weblogic.security.Security");
        } catch ( Exception e ) {
            logger.warn( "Unable to find weblogic.security.Security, disabling weblogic adapter" );
        }
    }

    @Override
    public List<Role> getRoles( final Principal principal,
                                final SecurityContext securityContext,
                                final RolesMode mode ) {
        List<Role> roles = new ArrayList<Role>();
        if ( webLogicSecurity == null ) {
            return roles;
        }

        if ( mode.equals( RolesMode.GROUP ) || mode.equals( RolesMode.BOTH ) ) {
            try {
                Method method = webLogicSecurity.getMethod("getCurrentSubject", new Class[]{});
                Subject wlsSubject = (Subject) method.invoke( null, new Object[]{ } );
                if ( wlsSubject != null ) {
                    for ( java.security.Principal p : wlsSubject.getPrincipals() ) {
                        if (p.getClass().getName().indexOf("WLSGroup") != -1) {
                            roles.add( new RoleImpl( p.getName() ) );
                        }
                    }
                }
            } catch ( Exception e ) {
                logger.error( "Unable to get groups from subject due to {}", e.getMessage(), e );
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
