/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.util;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.ext.security.server.RolesRegistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Unit test class for SecurityManagementUtils.</p>
 * 
 * @since 0.8.0
 */
public class SecurityManagementUtilsTest {

    @BeforeClass
    public static void setup() throws IOException {
        RolesRegistry.get().clear();
    }

    @Test
    public void testCreateGroup() {
        final String testId = "id1";
        final Group group = SecurityManagementUtils.createGroup(testId);
        assertNotNull(group);
        final String name = group.getName();
        assertEquals(name, testId);
    }

    @Test
    public void testCreateRole() {
        final String testId = "id1";
        final Role role = SecurityManagementUtils.createRole(testId);
        assertNotNull(role);
        final String name = role.getName();
        assertEquals(name, testId);
    }

    @Test
    public void testCreateUserWithId() {
        final String testId = "id1";
        final User user = SecurityManagementUtils.createUser(testId);
        assertNotNull(user);
        final String id = user.getIdentifier();
        assertEquals(id, testId);
    }

    @Test
    public void testCreateUserWithIdNull() {
        final String testId = null;
        assertNull(SecurityManagementUtils.createUser(testId));
    }

    @Test
    public void testCreateUserWithGroups() {
        final String testId = "id1";
        final String group1Id = "g1";
        final String group2Id = "g2";
        final Group group1 = SecurityManagementUtils.createGroup(group1Id);
        final Group group2 = SecurityManagementUtils.createGroup(group2Id);
        final Set<Group> groups = new HashSet<Group>(2);
        groups.add(group1);
        groups.add(group2);
        final User user = SecurityManagementUtils.createUser(testId, groups);
        assertNotNull(user);
        final String id = user.getIdentifier();
        assertEquals(id, testId);
        final Set<Group> resultGroups = user.getGroups();
        assertNotNull(resultGroups);
        assertTrue(resultGroups.size() == 2);
        assertEquals(resultGroups, groups);
    }

    @Test
    public void testCreateUserWithGroupsAndRoles() {
        final String testId = "id1";
        final String group1Id = "g1";
        final String group2Id = "g2";
        final String role1Id = "r1";
        final String role2Id = "r2";
        final Group group1 = SecurityManagementUtils.createGroup(group1Id);
        final Group group2 = SecurityManagementUtils.createGroup(group2Id);
        final Set<Group> groups = new HashSet<Group>(2);
        groups.add(group1);
        groups.add(group2);
        final  Role role1 = SecurityManagementUtils.createRole(role1Id);
        final  Role role2 = SecurityManagementUtils.createRole(role2Id);
        final Set<Role> roles = new HashSet<Role>(2);
        roles.add(role1);
        roles.add(role2);
        final User user = SecurityManagementUtils.createUser(testId, groups, roles);
        assertNotNull(user);
        final String id = user.getIdentifier();
        assertEquals(id, testId);
        final Set<Group> resultGroups = user.getGroups();
        assertNotNull(resultGroups);
        assertTrue(resultGroups.size() == 2);
        assertEquals(resultGroups, groups);
        final Set<Role> resultRoles = user.getRoles();
        assertNotNull(resultRoles);
        assertTrue(resultRoles.size() == 2);
        assertEquals(resultRoles, roles);
    }

    @Test
    public void testCreateUserWithGroupsAndRolesAndProperties() {
        final String testId = "id1";
        final String group1Id = "g1";
        final String group2Id = "g2";
        final String role1Id = "r1";
        final String role2Id = "r2";
        final Group group1 = SecurityManagementUtils.createGroup(group1Id);
        final Group group2 = SecurityManagementUtils.createGroup(group2Id);
        final Set<Group> groups = new HashSet<Group>(2);
        groups.add(group1);
        groups.add(group2);
        final  Role role1 = SecurityManagementUtils.createRole(role1Id);
        final  Role role2 = SecurityManagementUtils.createRole(role2Id);
        final Set<Role> roles = new HashSet<Role>(2);
        roles.add(role1);
        roles.add(role2);
        final Map<String, String> props = new HashMap<String, String>(2);
        props.put("p1", "value1");
        props.put("p2", "value2");
        final User user = SecurityManagementUtils.createUser(testId, groups, roles, props);
        assertNotNull(user);
        final String id = user.getIdentifier();
        assertEquals(id, testId);
        final Set<Group> resultGroups = user.getGroups();
        assertNotNull(resultGroups);
        assertTrue(resultGroups.size() == 2);
        assertEquals(resultGroups, groups);
        final Set<Role> resultRoles = user.getRoles();
        assertNotNull(resultRoles);
        assertTrue(resultRoles.size() == 2);
        assertEquals(resultRoles, roles);

        final Map<String, String> resultProps = user.getProperties();
        assertNotNull(resultProps);
        assertTrue(resultProps.size() == 2);
        assertEquals(resultProps.get("p1"), "value1");
        assertEquals(resultProps.get("p2"), "value2");
    }

    @Test
    public void testCloneUser() {
        final String testId = "id1";
        final String group1Id = "g1";
        final String group2Id = "g2";
        final String role1Id = "r1";
        final String role2Id = "r2";
        final Group group1 = SecurityManagementUtils.createGroup(group1Id);
        final Group group2 = SecurityManagementUtils.createGroup(group2Id);
        final Set<Group> groups = new HashSet<Group>(2);
        groups.add(group1);
        groups.add(group2);
        final  Role role1 = SecurityManagementUtils.createRole(role1Id);
        final  Role role2 = SecurityManagementUtils.createRole(role2Id);
        final Set<Role> roles = new HashSet<Role>(2);
        roles.add(role1);
        roles.add(role2);
        final Map<String, String> props = new HashMap<String, String>(2);
        props.put("p1", "value1");
        props.put("p2", "value2");
        final User user = SecurityManagementUtils.createUser(testId, groups, roles, props);
        final User cloned = SecurityManagementUtils.clone(user);
        assertNotNull(cloned);
        final String id = cloned.getIdentifier();
        assertEquals(id, testId);
        final Set<Group> resultGroups = cloned.getGroups();
        assertNotNull(resultGroups);
        assertTrue(resultGroups.size() == 2);
        assertEquals(resultGroups, groups);
        final Set<Role> resultRoles = cloned.getRoles();
        assertNotNull(resultRoles);
        assertTrue(resultRoles.size() == 2);
        assertEquals(resultRoles, roles);
        final Map<String, String> resultProps = cloned.getProperties();
        assertNotNull(resultProps);
        assertTrue(resultProps.size() == 2);
        assertEquals(resultProps.get("p1"), "value1");
        assertEquals(resultProps.get("p2"), "value2");
    }

    @Test
    public void testGetRegisteredRoles() {
        RolesRegistry.get().registerRole("role1");
        RolesRegistry.get().registerRole("role2");
        final Set<Role> roles = SecurityManagementUtils.getRegisteredRoles();
        assertNotNull(roles);
        assertTrue(roles.size() == 3);
        assertTrue(roles.contains(new RoleImpl("admin")));
    }

    @Test
    public void testGetRegisteredRoleNames() {
        RolesRegistry.get().registerRole("role1");
        RolesRegistry.get().registerRole("role2");
        final Set<String> roles = SecurityManagementUtils.getRegisteredRoleNames();
        assertNotNull(roles);
        assertTrue(roles.size() == 3);
        assertTrue(roles.contains("admin"));
    }

    @Test
    public void testRolesToString() {
        Set<Role> roles = new HashSet<Role>(2);
        Role role1 = mock(Role.class);
        when(role1.getName()).thenReturn("role1");
        Role role2 = mock(Role.class);
        when(role2.getName()).thenReturn("role2");
        roles.add(role1);
        roles.add(role2);
        final Set<String> rolesStr = SecurityManagementUtils.rolesToString(roles);
        assertNotNull(rolesStr);
        assertTrue(rolesStr.size() == 2);
        assertTrue(rolesStr.contains("role1"));
        assertTrue(rolesStr.contains("role2"));
    }

    @Test
    public void testRolesToStringEmpty() {
        final Set<String> rolesStr = SecurityManagementUtils.rolesToString(null);
        assertNotNull(rolesStr);
        assertTrue(rolesStr.size() == 0);
    }

    @Test
    public void testGroupsToString() {
        Set<Group> roles = new HashSet<Group>(2);
        Group role1 = mock(Group.class);
        when(role1.getName()).thenReturn("group1");
        Group role2 = mock(Group.class);
        when(role2.getName()).thenReturn("group2");
        roles.add(role1);
        roles.add(role2);
        final Set<String> rolesStr = SecurityManagementUtils.groupsToString(roles);
        assertNotNull(rolesStr);
        assertTrue(rolesStr.size() == 2);
        assertTrue(rolesStr.contains("group1"));
        assertTrue(rolesStr.contains("group2"));
    }

    @Test
    public void testGroupsToStringEmpty() {
        final Set<String> rolesStr = SecurityManagementUtils.groupsToString(null);
        assertNotNull(rolesStr);
        assertTrue(rolesStr.size() == 0);
    }

    @Test
    public void testPopulateGroupsOrRoles() {
        RolesRegistry.get().registerRole("role1");
        Set<String> registeredRoles = SecurityManagementUtils.getRegisteredRoleNames();
        Set<Group> groups  = new HashSet<Group>();
        Set<Role> roles = new HashSet<Role>();
        SecurityManagementUtils.populateGroupOrRoles("group1", registeredRoles, groups, roles);
        assertTrue(groups.size() == 1);
        assertTrue(roles.isEmpty());
        SecurityManagementUtils.populateGroupOrRoles("role1", registeredRoles, groups, roles);
        assertTrue(groups.size() == 1);
        assertTrue(roles.size() == 1);
    }
    
}
