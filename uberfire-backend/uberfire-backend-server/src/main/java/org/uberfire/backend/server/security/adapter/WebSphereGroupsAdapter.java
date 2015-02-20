package org.uberfire.backend.server.security.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.authz.adapter.GroupsAdapter;

@ApplicationScoped
public class WebSphereGroupsAdapter implements GroupsAdapter {

    private static final Logger logger = LoggerFactory.getLogger( WebSphereGroupsAdapter.class );
    private Object registry;

    public WebSphereGroupsAdapter() {
        try {
            this.registry = InitialContext.doLookup("UserRegistry");
        } catch ( NamingException e ) {
            logger.info( "Unable to look up UserRegistry in JNDI under key 'UserRegistry', disabling websphere adapter" );
        }
    }

    @Override
    public List<Group> getGroups( final String principal ) {
        if ( registry == null ) {
            return Collections.emptyList();
        }

        final List<Group> groups = new ArrayList<Group>();

        try {
            Method method = registry.getClass().getMethod( "getGroupsForUser", new Class[]{ String.class } );
            List rolesIn = (List) method.invoke( registry, new Object[]{ principal } );
            if ( rolesIn != null ) {
                for ( Object o : rolesIn ) {
                    groups.add( new GroupImpl( o.toString() ) );
                }
            }
        } catch ( Exception e ) {
            logger.error( "Unable to get groups from registry due to {}", e.getMessage(), e );
        }

        return groups;
    }
}
