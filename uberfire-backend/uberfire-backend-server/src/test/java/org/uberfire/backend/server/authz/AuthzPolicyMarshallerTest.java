/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.authz;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

import static org.junit.Assert.*;

public class AuthzPolicyMarshallerTest {

    AuthorizationPolicyBuilder builder;
    AuthorizationPolicyMarshaller marshaller;
    PermissionManager permissionManager;

    @Before
    public void setUp() {
        permissionManager = new DefaultPermissionManager(new DefaultPermissionTypeRegistry());
        builder = permissionManager.newAuthorizationPolicy();
        marshaller = new AuthorizationPolicyMarshaller();
    }

    @Test
    public void testHomeEntry() {
        List<String> tokens = marshaller.split("role.admin.home");
        assertEquals(tokens.size(), 3);
        assertEquals(tokens.get(0), "role");
        assertEquals(tokens.get(1), "admin");
        assertEquals(tokens.get(2), "home");
    }

    @Test
    public void testPriorityEntry() {
        List<String> tokens = marshaller.split("role.admin.priority");
        assertEquals(tokens.size(), 3);
        assertEquals(tokens.get(0), "role");
        assertEquals(tokens.get(1), "admin");
        assertEquals(tokens.get(2), "priority");
    }

    @Test
    public void testPermissionEntry() {
        List<String> tokens = marshaller.split("role.admin.permission.perspective.read");
        assertEquals(tokens.size(), 4);
        assertEquals(tokens.get(0), "role");
        assertEquals(tokens.get(1), "admin");
        assertEquals(tokens.get(2), "permission");
        assertEquals(tokens.get(3), "perspective.read");
    }

    @Test
    public void testPermissionEntry2() {
        List<String> tokens = marshaller.split("role.manager.permission.repository.update.git://repo1");
        assertEquals(tokens.size(), 4);
        assertEquals(tokens.get(0), "role");
        assertEquals(tokens.get(1), "manager");
        assertEquals(tokens.get(2), "permission");
        assertEquals(tokens.get(3), "repository.update.git://repo1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEntry1() {
        marshaller.split("role..priority");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEntry2() {
        marshaller.split(".admin.priority");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEntry3() {
        marshaller.split("role");
    }

    @Test
    public void testPolicyRead() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/classes/security-policy.properties");
        Path policyPath = Paths.get(fileURL.toURI());
        NonEscapedProperties input = new NonEscapedProperties();
        input.load(Files.newBufferedReader(policyPath));
        marshaller.read(builder, input);

        AuthorizationPolicy policy = builder.build();

        Set<Role> roles = policy.getRoles();
        assertEquals(roles.size(), 3);

        Role adminRole = new RoleImpl("admin");
        PermissionCollection permissions = policy.getPermissions(adminRole);
        assertTrue(roles.contains(adminRole));
        assertEquals(policy.getRoleDescription(adminRole), "Administrator");
        assertEquals(policy.getPriority(adminRole), 1);
        assertEquals(permissions.collection().size(), 3);

        Permission permission = permissions.get("perspective.read");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("perspective.read.SimplePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_DENIED);


        Role userRole = new RoleImpl("user");
        permissions = policy.getPermissions(userRole);
        assertTrue(roles.contains(userRole));
        assertEquals(policy.getRoleDescription(userRole), "End user");
        assertEquals(policy.getPriority(userRole), 2);
        assertEquals(permissions.collection().size(), 4);

        permission = permissions.get("perspective.read");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_DENIED);

        permission = permissions.get("perspective.read.HomePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("perspective.read.SimplePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);

        Role managerRole = new RoleImpl("manager");
        permissions = policy.getPermissions(managerRole);
        assertTrue(roles.contains(managerRole));
        assertEquals(policy.getRoleDescription(managerRole), "Manager");
        assertEquals(policy.getPriority(managerRole), 3);
        assertEquals(permissions.collection().size(), 3);

        permission = permissions.get("perspective.read");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("repository.read.git://repo1");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);
    }

    @Test
    public void testPolicyWrite() throws Exception {
        builder.role("admin").priority(5).home("A")
                .permission("p1", true)
                .permission("p2", false);
        builder.group("group1").priority(3).home("B")
                .permission("p1", false)
                .permission("p2", true);

        AuthorizationPolicy policy = builder.build();
        TreeMap<String,String> output = new TreeMap<>();
        marshaller.write(policy, output);

        assertEquals(output.size(), 8);
        assertEquals(output.get("role.admin.home"), "A");
        assertEquals(output.get("role.admin.priority"), "5");
        assertEquals(output.get("role.admin.permission.p1"), "true");
        assertEquals(output.get("role.admin.permission.p2"), "false");
        assertEquals(output.get("group.group1.home"), "B");
        assertEquals(output.get("group.group1.priority"), "3");
        assertEquals(output.get("group.group1.permission.p1"), "false");
        assertEquals(output.get("group.group1.permission.p2"), "true");
    }
}
