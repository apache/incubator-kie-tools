/*
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
package org.uberfire.security.impl.authz;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionType;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.authz.VotingStrategy;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationManagerTest {

    @Mock
    Resource perspective1;

    @Mock
    Resource perspective2;

    @Mock
    Resource perspective3;

    @Mock
    ResourceType perspectiveType;

    @Mock
    Resource resource1;

    @Mock
    RuntimeResource resource2;

    @Mock
    Resource menuPerspective1;

    @Mock
    Command onGranted;

    @Mock
    Command onDenied;

    @Mock
    Resource menuPerspective2;

    User user;
    DefaultAuthorizationManager authorizationManager;
    PermissionManager permissionManager;
    PermissionTypeRegistry permissionTypeRegistry;

    protected User createUserMock(String... roles) {
        User user = mock(User.class);
        Set<Role> roleSet = Stream.of(roles).map(RoleImpl::new).collect(Collectors.toSet());
        when(user.getRoles()).thenReturn(roleSet);
        when(user.getGroups()).thenReturn(null);
        return user;
    }

    @Before
    public void setUp() {
        user = createUserMock("admin");

        when(perspectiveType.getName()).thenReturn("perspective");

        when(resource1.getDependencies()).thenReturn(null);
        when(resource2.getDependencies()).thenReturn(null);

        when(perspective1.getIdentifier()).thenReturn("p1");
        when(perspective2.getIdentifier()).thenReturn("p2");
        when(perspective1.getDependencies()).thenReturn(null);
        when(perspective2.getDependencies()).thenReturn(null);
        when(perspective3.getDependencies()).thenReturn(null);
        when(perspective1.getResourceType()).thenReturn(perspectiveType);
        when(perspective2.getResourceType()).thenReturn(perspectiveType);
        when(perspective3.getResourceType()).thenReturn(perspectiveType);

        when(menuPerspective1.getDependencies()).thenReturn(Arrays.asList(perspective1));
        when(menuPerspective2.getDependencies()).thenReturn(Arrays.asList(perspective2));

        permissionTypeRegistry = new DefaultPermissionTypeRegistry();
        permissionManager = spy(new DefaultPermissionManager(permissionTypeRegistry));
        authorizationManager = new DefaultAuthorizationManager(permissionManager);

        permissionManager.setAuthorizationPolicy(
                permissionManager.newAuthorizationPolicy()
                        .role("admin").priority(0)
                        .permission("perspective.read",
                                    true)
                        .permission("perspective.read.p2",
                                    false)
                        .permission("custom.resource2",
                                    true)
                        .role("manager").priority(0)
                        .permission("perspective.read",
                                    false)
                        .role("developer").priority(10)
                        .permission("perspective.read",
                                    true)
                        .build());
    }

    @Test
    public void avoidPermissionTypesCollision() {
        PermissionType permissionType = mock(PermissionType.class);
        when(permissionType.getType()).thenReturn("type");
        permissionTypeRegistry.register(permissionType);

        assertThatThrownBy(() -> permissionTypeRegistry.register(permissionType))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PermissionType already exists: type");
    }

    @Test
    public void testUnknownResource() {
        boolean result = authorizationManager.authorize(resource1,
                                                        user);
        assertEquals(result,
                     true);
    }

    @Test
    public void testAuthorizationPolicyUndefined() {
        User user = createUserMock("role1");
        permissionManager.setAuthorizationPolicy(null);
        PermissionCollection pc = permissionManager.resolvePermissions(user,
                                                                       VotingStrategy.PRIORITY);
        boolean result = authorizationManager.authorize(resource1,
                                                        user);

        assertNotNull(pc);
        assertEquals(pc.collection().size(),
                     0);
        assertEquals(result,
                     true);
    }

    @Test
    public void testNonManagedResource() {
        boolean result = authorizationManager.authorize(resource2,
                                                        user);
        assertEquals(result,
                     true);
        verify(permissionManager,
               never()).checkPermission(any(Permission.class),
                                        any(User.class));
    }

    @Test
    public void testCustomResourceAccess() {
        when(resource2.getIdentifier()).thenReturn("custom.resource2");
        boolean result = authorizationManager.authorize(resource2,
                                                        user);
        assertEquals(result,
                     true);
        verify(permissionManager).checkPermission(any(Permission.class),
                                                  any(User.class),
                                                  eq(null));
    }

    @Test
    public void testResourceTypeAccess() {
        User user1 = createUserMock("manager");
        boolean result = authorizationManager.authorize(perspective3,
                                                        user1);
        assertEquals(result,
                     false);
    }

    @Test
    public void testPerspectiveAccessGranted() {
        boolean result = authorizationManager.authorize(perspective1,
                                                        user);
        assertEquals(result,
                     true);
        verify(permissionManager).checkPermission(any(Permission.class),
                                                  any(User.class),
                                                  eq(null));
    }

    @Test
    public void testPerspectiveAccessDenied() {
        boolean result = authorizationManager.authorize(perspective2,
                                                        user);
        assertEquals(result,
                     false);
        verify(permissionManager).checkPermission(any(Permission.class),
                                                  any(User.class),
                                                  eq(null));
    }

    @Test
    public void testMenuItemGranted() {
        boolean result = authorizationManager.authorize(menuPerspective1,
                                                        user);
        assertEquals(result,
                     true);
        verify(permissionManager).checkPermission(any(Permission.class),
                                                  any(User.class),
                                                  eq(null));
    }

    @Test
    public void testMenuItemDenied() {
        boolean result = authorizationManager.authorize(menuPerspective2,
                                                        user);
        assertEquals(result,
                     false);
    }

    @Test
    public void testMenuItemAbstain() {
        permissionManager.setAuthorizationPolicy(null);
        boolean result = authorizationManager.authorize(menuPerspective1,
                                                        user);
        assertEquals(result,
                     true);
    }

    @Test
    public void testMenuGroupGranted() {
        Resource resource = new ResourceRef(null,
                                            null,
                                            Arrays.asList(menuPerspective1,
                                                          menuPerspective2));
        boolean result = authorizationManager.authorize(resource,
                                                        user);
        assertEquals(result,
                     true);

        resource = new ResourceRef(null,
                                   null,
                                   Arrays.asList(menuPerspective1));
        result = authorizationManager.authorize(resource,
                                                user);
        assertEquals(result,
                     true);
    }

    @Test
    public void testMenuGroupDenied() {
        Resource resource = new ResourceRef(null,
                                            null,
                                            Arrays.asList(menuPerspective2));
        boolean result = authorizationManager.authorize(resource,
                                                        user);
        assertEquals(result,
                     false);
    }

    @Test
    public void testEmptyMenuGranted() {
        Resource resource = new ResourceRef(null,
                                            null,
                                            Arrays.asList());
        boolean result = authorizationManager.authorize(resource,
                                                        user);
        assertEquals(result,
                     true);
    }

    @Test
    public void testPermissionGranted() {
        boolean result = authorizationManager.authorize("perspective.read.p1",
                                                        user);
        assertEquals(result,
                     true);
    }

    @Test
    public void testPermissionDenied() {
        boolean result = authorizationManager.authorize("perspective.read.p2",
                                                        user);
        assertEquals(result,
                     false);
    }

    @Test
    public void testGrantCommandInvoked() throws Exception {
        authorizationManager.check(perspective1,
                                   user).granted(onGranted);
        verify(onGranted).execute();

        reset(onGranted);
        authorizationManager.check(perspective1,
                                   user).granted(onGranted).denied(onDenied);
        verify(onGranted).execute();
        verify(onDenied,
               never()).execute();
    }

    @Test
    public void testGrantCommandNotInvoked() throws Exception {
        authorizationManager.check(perspective2,
                                   user).granted(onGranted);
        verify(onGranted,
               never()).execute();
    }

    @Test
    public void testDenyCommandInvoked() throws Exception {
        authorizationManager.check(perspective2,
                                   user).denied(onDenied);
        verify(onDenied).execute();

        reset(onDenied);
        authorizationManager.check(perspective2,
                                   user).granted(onGranted).denied(onDenied);
        verify(onGranted,
               never()).execute();
        verify(onDenied).execute();
    }

    @Test
    public void testDenyCommandNotInvoked() throws Exception {
        authorizationManager.check(perspective1,
                                   user).denied(onDenied);
        verify(onDenied,
               never()).execute();
    }

    @Test
    public void testPermissionCheck() throws Exception {
        authorizationManager.check("perspective.read.p1",
                                   user)
                .granted(onGranted)
                .denied(onDenied);
        verify(onGranted).execute();
        verify(onDenied,
               never()).execute();
    }

    @Test
    public void testVotingPriority() throws Exception {
        User user1 = createUserMock("admin",
                                    "developer");
        permissionManager.setDefaultVotingStrategy(VotingStrategy.PRIORITY);
        assertTrue(authorizationManager.authorize(perspective2,
                                                  user1));
    }

    @Test
    public void testSamePriorityVoting() {
        User user = createUserMock("role1",
                                   "role2");
        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy()
                                                         .role("role1")
                                                         .permission("perspective.read",
                                                                     false)
                                                         .permission("perspective.read.p1",
                                                                     true)
                                                         .permission("screen.read.s1",
                                                                     true)
                                                         .role("role2")
                                                         .permission("perspective.read",
                                                                     true)
                                                         .permission("perspective.read.p1",
                                                                     false)
                                                         .permission("screen.read",
                                                                     false)
                                                         .build());

        permissionManager.setDefaultVotingStrategy(VotingStrategy.PRIORITY);
        assertTrue(authorizationManager.authorize("perspective.read",
                                                  user));
        assertTrue(authorizationManager.authorize("perspective.read.p1",
                                                  user));
        assertTrue(authorizationManager.authorize("perspective.read.p2",
                                                  user));
        assertFalse(authorizationManager.authorize("screen.read",
                                                   user));
        assertTrue(authorizationManager.authorize("screen.read.s1",
                                                  user));
    }

    @Test
    public void testHighPriorityVoting() {
        User user = createUserMock("role1",
                                   "role2",
                                   "role3");
        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy()
                                                         .role("role1").priority(10)
                                                         .permission("perspective.read",
                                                                     false)
                                                         .permission("perspective.read.p1",
                                                                     true)
                                                         .permission("screen.read.s1",
                                                                     true)
                                                         .role("role2")
                                                         .permission("perspective.read",
                                                                     true)
                                                         .permission("perspective.read.p1",
                                                                     false)
                                                         .permission("screen.read",
                                                                     false)
                                                         .role("role3").priority(5)
                                                         .permission("perspective.read",
                                                                     true)
                                                         .permission("perspective.read.p1",
                                                                     false)
                                                         .permission("screen.read",
                                                                     false)
                                                         .build());

        permissionManager.setDefaultVotingStrategy(VotingStrategy.PRIORITY);
        assertFalse(authorizationManager.authorize("perspective.read",
                                                   user));
        assertTrue(authorizationManager.authorize("perspective.read.p1",
                                                  user));
        assertFalse(authorizationManager.authorize("perspective.read.p2",
                                                   user));
        assertFalse(authorizationManager.authorize("screen.read",
                                                   user));
        assertTrue(authorizationManager.authorize("screen.read.s1",
                                                  user));
    }

    @Test
    public void testLowPriorityVoting() {
        User user = createUserMock("role1",
                                   "role2");
        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy()
                                                         .role("role1")
                                                         .permission("perspective.read",
                                                                     false)
                                                         .permission("perspective.read.p1",
                                                                     true)
                                                         .permission("screen.read.s1",
                                                                     true)
                                                         .role("role2").priority(10)
                                                         .permission("perspective.read",
                                                                     true)
                                                         .permission("perspective.read.p1",
                                                                     false)
                                                         .permission("screen.read",
                                                                     false)
                                                         .build());

        permissionManager.setDefaultVotingStrategy(VotingStrategy.PRIORITY);
        assertTrue(authorizationManager.authorize("perspective.read",
                                                  user));
        assertFalse(authorizationManager.authorize("perspective.read.p1",
                                                   user));
        assertTrue(authorizationManager.authorize("perspective.read.p2",
                                                  user));
        assertFalse(authorizationManager.authorize("screen.read",
                                                   user));
        assertFalse(authorizationManager.authorize("screen.read.s1",
                                                   user));
    }

    @Test
    public void testVotingUnanimous() throws Exception {
        User user1 = createUserMock("admin",
                                    "manager");
        permissionManager.setDefaultVotingStrategy(VotingStrategy.UNANIMOUS);
        assertFalse(authorizationManager.authorize(perspective1,
                                                   user1));

        authorizationManager.check(perspective1,
                                   user1)
                .granted(onGranted)
                .denied(onDenied);
        verify(onGranted,
               never()).execute();
        verify(onDenied).execute();
    }

    @Test
    public void testVotingAffirmative() throws Exception {
        User user1 = createUserMock("admin",
                                    "manager");
        permissionManager.setDefaultVotingStrategy(VotingStrategy.AFFIRMATIVE);
        assertTrue(authorizationManager.authorize(perspective1,
                                                  user1));

        authorizationManager.check(perspective1,
                                   user1)
                .granted(onGranted)
                .denied(onDenied);
        verify(onDenied,
               never()).execute();
        verify(onGranted).execute();
    }

    @Test
    public void testInvalidateCache() throws Exception {
        User user1 = createUserMock("admin",
                "manager");
        permissionManager.setDefaultVotingStrategy(VotingStrategy.AFFIRMATIVE);
        assertTrue(authorizationManager.authorize(perspective1,
                user1));

        authorizationManager.check(perspective1,
                user1)
                .granted(onGranted)
                .denied(onDenied);
        verify(onDenied,
                never()).execute();
        verify(onGranted).execute();

        authorizationManager.invalidate(user1);
        verify(permissionManager, times(1)).invalidate(user1);
    }
}
