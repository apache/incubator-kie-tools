/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.util;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.ext.security.management.api.Capability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>User system management helper class shared between backend and client side of the application.</p>
 *
 * @since 0.8.0
 */
public class SecurityManagementUtils {

    public static final Capability[] USERS_CAPABILITIES = new Capability[] { 
            Capability.CAN_SEARCH_USERS, Capability.CAN_ADD_USER, Capability.CAN_UPDATE_USER, 
    Capability.CAN_READ_USER, Capability.CAN_DELETE_USER, Capability.CAN_MANAGE_ATTRIBUTES,
    Capability.CAN_ASSIGN_GROUPS, Capability.CAN_ASSIGN_ROLES, Capability.CAN_CHANGE_PASSWORD};

    public static final Capability[] GROUPS_CAPABILITIES = new Capability[] { 
            Capability.CAN_SEARCH_GROUPS, Capability.CAN_ADD_GROUP, Capability.CAN_UPDATE_GROUP,
            Capability.CAN_READ_GROUP, Capability.CAN_DELETE_GROUP};

    public static final Capability[] ROLES_CAPABILITIES = new Capability[] { 
            Capability.CAN_SEARCH_ROLES, Capability.CAN_ADD_ROLE, Capability.CAN_UPDATE_ROLE,
            Capability.CAN_READ_ROLE, Capability.CAN_DELETE_ROLE};

    public static User createUser(final String id) {
        return createUser(id, null);
    }
    
    public static User createUser(final String id, final Set<Group> groups) {
        return createUser(id, groups, null);
    }
    
    public static User createUser(final String id, final Set<Group> groups, final Set<Role> roles) {
        return createUser(id, groups, roles, null);
    }
    
    public static User createUser(final String id, final Set<Group> groups, final Set<Role> roles, final Map<String, String> properties) {
        if (id == null) return null;
        final Set<Group> _groups = groups != null ? new HashSet<Group>(groups) : new HashSet<Group>(0);
        final Set<Role> _roles = roles != null ? new HashSet<Role>(roles) : new HashSet<Role>(0);
        final Map<String, String> _properties = properties != null ? new HashMap<String, String>(properties) : new HashMap<String, String>(0);
        return new UserImpl(id, _roles, _groups, _properties);
    }
    
    public static Group createGroup(final String name) {
        if (name == null) return null;
        return new GroupImpl(name);
    }

    public static Role createRole(final String name) {
        if (name == null) return null;
        return new RoleImpl(name);
    }
    
    public static User clone(final User user) {
        if (user == null) return null;
        final String id = user.getIdentifier();
        final Set<Group> groups = user.getGroups() != null ? new HashSet<Group>(user.getGroups()) : new HashSet<Group>(0);
        final Set<Role> roles = user.getRoles() != null ? new HashSet<Role>(user.getRoles()) : new HashSet<Role>(0);
        final Map<String, String> properties = user.getProperties() != null ? new HashMap<String, String>(user.getProperties()) : new HashMap<String, String>(0);
        return new UserImpl(id, roles, groups, properties);
    }
}
