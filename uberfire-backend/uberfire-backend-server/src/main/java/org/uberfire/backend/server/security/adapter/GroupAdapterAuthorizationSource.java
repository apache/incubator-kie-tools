package org.uberfire.backend.server.security.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.uberfire.security.authz.adapter.GroupsAdapter;

public class GroupAdapterAuthorizationSource {

    private final ServiceLoader<GroupsAdapter> groupsAdapterServiceLoader = ServiceLoader.load( GroupsAdapter.class );

    public Set<Group> collectGroups(String name) {

        Set<Group> userGroups = new HashSet<Group>();
        for ( final GroupsAdapter adapter : groupsAdapterServiceLoader ) {
            final List<Group> groupRoles = adapter.getGroups( name );
            if ( groupRoles != null ) {
                userGroups.addAll( groupRoles );
            }
        }

        return userGroups;
    }

    public Set<Role> collectGroupsAsRoles(String name) {

        Set<Role> userGroups = new HashSet<Role>();
        for ( final GroupsAdapter adapter : groupsAdapterServiceLoader ) {
            final List<Group> groupRoles = adapter.getGroups( name );
            if ( groupRoles != null ) {
                for (Group group : groupRoles) {
                    userGroups.add(new RoleImpl(group.getName()));
                }
            }
        }

        return userGroups;
    }
}
