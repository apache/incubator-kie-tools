package org.uberfire.security.server.auth.source.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.uberfire.security.Role;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.server.RolesRegistry;

import static javax.ejb.TransactionAttributeType.*;

@Singleton
@Startup
@TransactionAttribute(NOT_SUPPORTED)
public class WebSphereRoleProviderServices {

    private static WebSphereRoleProviderServices instance;

    @PostConstruct
    public void init() {
        try {
            instance = InitialContext.doLookup( "java:module/WebSphereRoleProviderServices" );
        } catch ( final Exception ignored ) {
        }
    }

    public Collection<Role> getRoles() {
        try {
            final List<Role> roles = new ArrayList<Role>();
            final SessionContext sctxLookup = InitialContext.doLookup( "java:comp/EJBContext" );

            for ( final Role enforcementRole : RolesRegistry.get().getRegisteredRoles() ) {
                if ( sctxLookup.isCallerInRole( enforcementRole.getName() ) ) {
                    roles.add( new RoleImpl( enforcementRole.getName() ) );
                }
            }

            return roles;
        } catch ( NamingException e ) {
            return Collections.emptyList();
        }
    }

}
