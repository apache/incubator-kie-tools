/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server.security.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

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
    public List<Group> getGroups( final String principal, final Object subject ) {
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
