/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.security.auth.Subject;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.uberfire.security.authz.adapter.GroupsAdapter;

public class GroupAdapterAuthorizationSource {

    private final ServiceLoader<GroupsAdapter> groupsAdapterServiceLoader = ServiceLoader.load( GroupsAdapter.class );

    public Set<Group> collectGroups(String name) {

        Set<Group> userGroups = new HashSet<Group>();
        for ( final GroupsAdapter adapter : groupsAdapterServiceLoader ) {
            final List<Group> groupRoles = adapter.getGroups( name, null );
            if ( groupRoles != null ) {
                userGroups.addAll( groupRoles );
            }
        }

        return userGroups;
    }

    public Set<Role> collectGroupsAsRoles(String name, final Object subject) {

        Set<Role> userGroups = new HashSet<Role>();
        for ( final GroupsAdapter adapter : groupsAdapterServiceLoader ) {
            final List<Group> groupRoles = adapter.getGroups( name, subject );
            if ( groupRoles != null ) {
                for (Group group : groupRoles) {
                    userGroups.add(new RoleImpl(group.getName()));
                }
            }
        }

        return userGroups;
    }
}
