/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    protected User createUserMock(String... roles) {
        User user = mock(User.class);
        Set<Role> roleSet = Stream.of(roles).map(RoleImpl::new).collect(Collectors.toSet());
        when(user.getRoles()).thenReturn(roleSet);
        when(user.getGroups()).thenReturn(null);
        return user;
    }

    @Test
    public void testDefaultHomeEntry() {
        AuthorizationPolicyMarshaller.Key key = marshaller.parse("default.home");
        assertTrue(key.isDefault());
        assertEquals(key.getAttributeType(),
                     "home");
    }

    @Test
    public void testDefaultPermissionEntry() {
        AuthorizationPolicyMarshaller.Key key = marshaller.parse("default.permission.perspective.read");
        assertTrue(key.isDefault());
        assertEquals(key.getAttributeType(),
                     "permission");
        assertEquals(key.getAttributeId(),
                     "perspective.read");
    }

    @Test
    public void testOverwriteDefault() {
        Map<String, String> input = new HashMap<>();
        input.put("default.permission.perspective.read",
                  "false");
        input.put("default.permission.perspective.read.HomePerspective",
                  "true");
        input.put("role.user.permission.perspective.read",
                  "false");
        input.put("role.user.permission.perspective.read.HomePerspective",
                  "true");
        input.put("role.user.permission.perspective.read.Sales dashboard",
                  "true");

        marshaller.read(builder,
                        input);
        permissionManager.setAuthorizationPolicy(builder.build());

        User user = createUserMock("user",
                                   "manager");
        PermissionCollection pc = permissionManager.resolvePermissions(user,
                                                                       VotingStrategy.PRIORITY);
        assertEquals(pc.collection().size(),
                     3);
        assertEquals(pc.get("perspective.read").getResult(),
                     AuthorizationResult.ACCESS_DENIED);
        assertEquals(pc.get("perspective.read.HomePerspective").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
        assertEquals(pc.get("perspective.read.Sales dashboard").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
    }

    @Test
    public void testDefaultPermissionsNotOverwrite() {
        Map<String, String> input = new HashMap<>();
        input.put("default.permission.perspective.read.p1", "false");
        input.put("default.permission.perspective.read.p2", "false");
        input.put("role.user.permission.perspective.read", "true");
        input.put("role.user.permission.perspective.read.p2", "false");

        marshaller.read(builder, input);
        permissionManager.setAuthorizationPolicy(builder.build());

        User user = createUserMock("user");
        PermissionCollection pc = permissionManager.resolvePermissions(user, VotingStrategy.PRIORITY);
        assertEquals(pc.collection().size(), 2);
        assertEquals(pc.get("perspective.read").getResult(), AuthorizationResult.ACCESS_GRANTED);
        assertNull(pc.get("perspective.read.p1"));
        assertEquals(pc.get("perspective.read.p2").getResult(), AuthorizationResult.ACCESS_DENIED);
    }

    @Test
    public void testHomeEntry() {
        AuthorizationPolicyMarshaller.Key key = marshaller.parse("role.admin.home");
        assertTrue(key.isRole());
        assertEquals(key.getRole(),
                     "admin");
        assertEquals(key.getAttributeType(),
                     "home");
        assertNull(key.getAttributeId());
    }

    @Test
    public void testGroupEntry() {
        AuthorizationPolicyMarshaller.Key key = marshaller.parse("group.IT.home");
        assertFalse(key.isRole());
        assertTrue(key.isGroup());
        assertEquals(key.getGroup(),
                     "IT");
        assertEquals(key.getAttributeType(),
                     "home");
        assertNull(key.getAttributeId());
    }

    @Test
    public void testPriorityEntry() {
        AuthorizationPolicyMarshaller.Key key = marshaller.parse("role.admin.priority");
        assertTrue(key.isRole());
        assertEquals(key.getRole(),
                     "admin");
        assertEquals(key.getAttributeType(),
                     "priority");
        assertNull(key.getAttributeId());
    }

    @Test
    public void testPermissionEntry() {
        AuthorizationPolicyMarshaller.Key key = marshaller.parse("role.admin.permission.perspective.read");
        assertTrue(key.isRole());
        assertEquals(key.getRole(),
                     "admin");
        assertEquals(key.getAttributeType(),
                     "permission");
        assertEquals(key.getAttributeId(),
                     "perspective.read");
    }

    @Test
    public void testSpecialCharsAllowed() {
        AuthorizationPolicyMarshaller.Key key = marshaller.parse("role.manager.permission.repository.update.git://repo1");
        assertTrue(key.isRole());
        assertEquals(key.getRole(),
                     "manager");
        assertEquals(key.getAttributeType(),
                     "permission");
        assertEquals(key.getAttributeId(),
                     "repository.update.git://repo1");
    }

    @Test
    public void testRoleMissing() {
        assertThatThrownBy(() -> marshaller.parse("role..priority"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role value is empty");
    }

    @Test
    public void testTypeMissing() {
        assertThatThrownBy(() -> marshaller.parse(".admin.priority"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Key must start with [default|role|group]");
    }

    @Test
    public void testIncompleteEntry() {
        assertThatThrownBy(() -> marshaller.parse("role"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role value is empty");
    }

    @Test
    public void testReadDefaultEntries() {
        AuthorizationPolicy policy = builder.bydefault().home("B")
                .permission("p1",
                            false)
                .permission("p2",
                            true)
                .role("admin")
                .permission("p1",
                            true)
                .build();

        String home = policy.getHomePerspective();
        PermissionCollection pc = policy.getPermissions();

        assertEquals(home,
                     "B");
        assertEquals(pc.collection().size(),
                     2);
        assertNotNull(pc.get("p1"));
        assertNotNull(pc.get("p2"));
        assertEquals(pc.get("p1").getResult(),
                     AuthorizationResult.ACCESS_DENIED);
        assertEquals(pc.get("p2").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        Role admin = new RoleImpl("admin");
        home = policy.getHomePerspective(admin);
        pc = policy.getPermissions(admin);
        assertEquals(home,
                     "B");
        assertEquals(pc.collection().size(),
                     2);
        assertNotNull(pc.get("p1"));
        assertNotNull(pc.get("p2"));
        assertEquals(pc.get("p1").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
        assertEquals(pc.get("p2").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
    }

    @Test
    public void testPolicyRead() throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/classes/security-policy.properties");
        Path policyPath = Paths.get(fileURL.toURI());
        NonEscapedProperties input = new NonEscapedProperties();
        input.load(Files.newBufferedReader(policyPath));
        marshaller.read(builder,
                        input);

        AuthorizationPolicy policy = builder.build();

        Set<Role> roles = policy.getRoles();
        assertEquals(roles.size(),
                     3);

        Role adminRole = new RoleImpl("admin");
        PermissionCollection permissions = policy.getPermissions(adminRole);
        assertTrue(roles.contains(adminRole));
        assertEquals(policy.getRoleDescription(adminRole),
                     "Administrator");
        assertEquals(policy.getPriority(adminRole),
                     1);
        assertEquals(permissions.collection().size(),
                     3);

        Permission permission = permissions.get("perspective.read");
        assertNotNull(permission);
        assertEquals(permission.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("perspective.read.SimplePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(),
                     AuthorizationResult.ACCESS_DENIED);

        Role userRole = new RoleImpl("user");
        permissions = policy.getPermissions(userRole);
        assertTrue(roles.contains(userRole));
        assertEquals(policy.getRoleDescription(userRole),
                     "End user");
        assertEquals(policy.getPriority(userRole),
                     2);
        assertEquals(permissions.collection().size(),
                     4);

        permission = permissions.get("perspective.read");
        assertNotNull(permission);
        assertEquals(permission.getResult(),
                     AuthorizationResult.ACCESS_DENIED);

        permission = permissions.get("perspective.read.HomePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("perspective.read.SimplePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        Role managerRole = new RoleImpl("manager");
        permissions = policy.getPermissions(managerRole);
        assertTrue(roles.contains(managerRole));
        assertEquals(policy.getRoleDescription(managerRole),
                     "Manager");
        assertEquals(policy.getPriority(managerRole),
                     3);
        assertEquals(permissions.collection().size(),
                     3);

        permission = permissions.get("perspective.read");
        assertNotNull(permission);
        assertEquals(permission.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("repository.read.git://repo1");
        assertNotNull(permission);
        assertEquals(permission.getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
    }

    @Test
    public void testPolicyWrite() {
        builder.role("admin").priority(5).home("A")
                .permission("p1",
                            true)
                .permission("p2",
                            false)
                .group("group1").priority(3).home("B")
                .permission("p1",
                            false)
                .permission("p2",
                            true)
                .bydefault().home("B")
                .permission("p1",
                            false)
                .permission("p2",
                            true);

        AuthorizationPolicy policy = builder.build();
        TreeMap<String, String> output = new TreeMap<>();
        marshaller.write(policy,
                         output);

        assertEquals(output.size(),
                     11);
        assertEquals(output.get("role.admin.home"),
                     "A");
        assertEquals(output.get("role.admin.home"),
                     "A");
        assertEquals(output.get("role.admin.priority"),
                     "5");
        assertEquals(output.get("role.admin.permission.p1"),
                     "true");
        assertEquals(output.get("role.admin.permission.p2"),
                     "false");
        assertEquals(output.get("group.group1.home"),
                     "B");
        assertEquals(output.get("group.group1.priority"),
                     "3");
        assertEquals(output.get("group.group1.permission.p1"),
                     "false");
        assertEquals(output.get("group.group1.permission.p2"),
                     "true");
        assertEquals(output.get("default.home"),
                     "B");
        assertEquals(output.get("default.permission.p1"),
                     "false");
        assertEquals(output.get("default.permission.p2"),
                     "true");
    }

    @Test
    public void testPolicyRemove() {
        builder.group("group2").priority(3).home("B")
                .permission("p1",
                            false)
                .permission("p2",
                            true);

        AuthorizationPolicy policy = builder.build();
        TreeMap<String, String> output = new TreeMap<>();
        marshaller.write(policy,
                         output);
        Group g = new GroupImpl("group2");
        assertEquals("B", output.get("group.group2.home"));
        assertEquals("3", output.get("group.group2.priority"));
        assertEquals("false", output.get("group.group2.permission.p1"));
        assertEquals("true", output.get("group.group2.permission.p2"));
        marshaller.remove(g, policy, output);

        assertEquals(null, output.get("group.group2.home"));
        assertEquals(null, output.get("group.group2.priority"));
        assertEquals(null, output.get("group.group2.permission.p1"));
        assertEquals(null, output.get("group.group2.permission.p2"));
    }
}
