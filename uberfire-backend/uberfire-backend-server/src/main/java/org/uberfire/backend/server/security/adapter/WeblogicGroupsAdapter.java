package org.uberfire.backend.server.security.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.security.auth.Subject;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.authz.adapter.GroupsAdapter;

@ApplicationScoped
public class WeblogicGroupsAdapter implements GroupsAdapter {

    private static final Logger logger = LoggerFactory.getLogger( WeblogicGroupsAdapter.class );
    private Class webLogicSecurity;

    public WeblogicGroupsAdapter() {
        try {
            this.webLogicSecurity = Class.forName("weblogic.security.Security");
        } catch ( Exception e ) {
            logger.info( "Unable to find weblogic.security.Security, disabling weblogic adapter" );
        }
    }

    @Override
    public List<Group> getGroups( final String principal ) {
        if ( webLogicSecurity == null ) {
            return Collections.emptyList();
        }

        final List<Group> groups = new ArrayList<Group>();

        try {
            Method method = webLogicSecurity.getMethod( "getCurrentSubject", new Class[]{ } );
            Subject wlsSubject = (Subject) method.invoke( null, new Object[]{ } );
            if ( wlsSubject != null ) {
                for ( java.security.Principal p : wlsSubject.getPrincipals() ) {
                    if ( p.getClass().getName().indexOf( "WLSGroup" ) != -1 ) {
                        groups.add( new GroupImpl( p.getName() ) );
                    }
                }
            }
        } catch ( Exception e ) {
            logger.error( "Unable to get groups from subject due to {}", e.getMessage(), e );
        }

        return groups;
    }
}
