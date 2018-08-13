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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.VotingAlgorithm;
import org.uberfire.security.authz.VotingStrategy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_ABSTAIN;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_DENIED;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_GRANTED;

@RunWith(MockitoJUnitRunner.class)
public class PermissionManagerTest {

    PermissionManager permissionManager;
    DefaultAuthzResultCache authzResultCache;
    AuthorizationPolicy authorizationPolicy;
    Permission viewAll = new DotNamedPermission("resource.read",
                                                true);
    Permission denyAll = new DotNamedPermission("resource.read",
                                                false);
    Permission view1 = new DotNamedPermission("resource.read.1",
                                              true);
    Permission noView1 = new DotNamedPermission("resource.read.1",
                                                false);
    Permission view2 = new DotNamedPermission("resource.read.2",
                                              true);
    Permission view12 = new DotNamedPermission("resource.read.1.2",
                                               true);

    protected User createUserMock(String... roles) {
        User user = mock(User.class);
        Set<Role> roleSet = Stream.of(roles).map(RoleImpl::new).collect(Collectors.toSet());
        when(user.getIdentifier()).thenReturn(Integer.toString(user.hashCode()));
        when(user.getRoles()).thenReturn(roleSet);
        when(user.getGroups()).thenReturn(null);
        return user;
    }

    @Before
    public void setUp() {
        authzResultCache = spy(new DefaultAuthzResultCache());
        permissionManager = spy(new DefaultPermissionManager(new DefaultPermissionTypeRegistry(),
                                                             authzResultCache));
        permissionManager.setAuthorizationPolicy(
                authorizationPolicy = spy(permissionManager.newAuthorizationPolicy()
                                                  .role("viewAll").permission("resource.read",
                                                                              true)
                                                  .role("noViewAll").permission("resource.read",
                                                                                false)
                                                  .role("onlyView1",
                                                        5).permission("resource.read",
                                                                      false).permission("resource.read.1",
                                                                                        true)
                                                  .role("noView1").permission("resource.read.1",
                                                                              false)
                                                  .role("onlyView12").permission("resource.read.1.2",
                                                                                 true)
                                                  .build()));
    }

    @Test
    public void testSetNullPolicy() {
        permissionManager.setAuthorizationPolicy(null);
        AuthorizationPolicy policy = permissionManager.getAuthorizationPolicy();
        assertNotNull(policy);
        assertTrue(policy.getRoles().isEmpty());
        assertTrue(policy.getGroups().isEmpty());
    }

    @Test
    public void testCreateGlobalPermissions() {
        ResourceType type = () -> "type";
        Permission p = permissionManager.createPermission(type,
                                                          null,
                                                          true);
        assertEquals(p.getName(),
                     "type.read");

        p = permissionManager.createPermission(type,
                                               () -> "edit",
                                               true);
        assertEquals(p.getName(),
                     "type.edit");
    }

    @Test
    public void testCreateTypedPermissions() {
        ResourceType type = () -> "type";
        ResourceRef r = new ResourceRef("r1",
                                        type,
                                        null);
        Permission p = permissionManager.createPermission(r,
                                                          null,
                                                          true);
        assertEquals(p.getName(),
                     "type.read.r1");

        p = permissionManager.createPermission(r,
                                               ResourceAction.READ,
                                               true);
        assertEquals(p.getName(),
                     "type.read.r1");
    }

    @Test
    public void testUnknownTypePermissions() {
        ResourceRef r = new ResourceRef("r1",
                                        ResourceType.UNKNOWN,
                                        null);
        Permission p = permissionManager.createPermission(r,
                                                          null,
                                                          true);
        assertEquals(p.getName(),
                     "r1");

        p = permissionManager.createPermission(r,
                                               ResourceAction.READ,
                                               true);
        assertEquals(p.getName(),
                     "r1");
    }

    @Test
    public void testCreateNonTypedPermissions() {
        ResourceRef r = new ResourceRef("r1",
                                        null,
                                        null);
        Permission p = permissionManager.createPermission(r,
                                                          null,
                                                          true);
        assertEquals(p.getName(),
                     "r1");

        p = permissionManager.createPermission(r,
                                               ResourceAction.READ,
                                               true);
        assertEquals(p.getName(),
                     "r1");
    }

    @Test
    public void testResolveResourceId() {
        ResourceType type = () -> "type";
        ResourceRef r = new ResourceRef("r1",
                                        type,
                                        null);
        Permission p = permissionManager.createPermission(r,
                                                          null,
                                                          true);
        assertEquals(p.getName(),
                     "type.read.r1");

        String id = permissionManager.resolveResourceId(p);
        assertEquals(id,
                     "r1");
    }

    @Test
    public void testResolveResourceNull() {
        Permission p = permissionManager.createPermission("feature",
                                                          true);
        assertEquals(p.getName(),
                     "feature");

        String id = permissionManager.resolveResourceId(p);
        assertNull(id);
    }

    @Test
    public void testCheckPermission1() {
        User user = createUserMock("viewAll");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_DENIED);
    }

    @Test
    public void testCheckPermission2() {
        User user = createUserMock("viewAll",
                                   "onlyView1");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_DENIED);
    }

    @Test
    public void testCheckPermission3() {
        User user = createUserMock("viewAll",
                                   "onlyView1",
                                   "noView1");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_DENIED);
    }

    @Test
    public void testCheckPermission4() {
        User user = createUserMock("viewAll",
                                   "noView1");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_DENIED);
    }

    @Test
    public void testCheckPermission5() {
        User user = createUserMock("onlyView1");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_DENIED);
    }

    @Test
    public void testCheckPermission6() {
        User user = createUserMock("noView1");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_GRANTED);
    }

    @Test
    public void testCheckPermission7() {
        User user = createUserMock("onlyView1",
                                   "noView1");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_DENIED);
    }

    @Test
    public void testCheckPermission8() {
        User user = createUserMock("noView1",
                                   "onlyView12");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user),
                     ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view2,
                                                       user),
                     ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(view12,
                                                       user),
                     ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(noView1,
                                                       user),
                     ACCESS_GRANTED);
    }

    @Test
    public void testCacheHits() {
        User user = createUserMock("viewAll");
        permissionManager.checkPermission(viewAll,
                                          user);
        permissionManager.checkPermission(viewAll,
                                          user);
        permissionManager.checkPermission(viewAll,
                                          user);
        permissionManager.checkPermission(viewAll,
                                          user);
        verify(permissionManager,
               times(1)).resolvePermissions(user,
                                            VotingStrategy.PRIORITY);
        verify(authzResultCache,
               times(1)).put(user,
                             viewAll,
                             AuthorizationResult.ACCESS_GRANTED);
        verify(authzResultCache,
               times(4)).get(user,
                             viewAll);
        assertEquals(authzResultCache.size(user),
                     1);
        assertEquals(authzResultCache.size(createUserMock()),
                     0);

        permissionManager.invalidate(user);

        assertEquals(authzResultCache.size(user),
                0);

        permissionManager.checkPermission(viewAll,
                user);
        verify(permissionManager,
                times(2)).resolvePermissions(user,
                VotingStrategy.PRIORITY);
        verify(authzResultCache,
                times(2)).put(user,
                viewAll,
                AuthorizationResult.ACCESS_GRANTED);
        verify(authzResultCache,
                times(5)).get(user,
                viewAll);
        assertEquals(authzResultCache.size(user),
                1);
        assertEquals(authzResultCache.size(createUserMock()),
                0);
    }

    @Test
    public void testDefaultVotingStrategy() {
        User user = createUserMock("role1");
        assertEquals(permissionManager.getDefaultVotingStrategy(),
                     VotingStrategy.PRIORITY);

        VotingAlgorithm unanimousVoter = mock(VotingAlgorithm.class);
        when(unanimousVoter.vote(any())).thenReturn(AuthorizationResult.ACCESS_GRANTED);

        permissionManager.setDefaultVotingStrategy(VotingStrategy.UNANIMOUS);
        permissionManager.setVotingAlgorithm(VotingStrategy.UNANIMOUS,
                                             unanimousVoter);
        permissionManager.checkPermission(viewAll,
                                          user);
        verify(unanimousVoter).vote(any());

        permissionManager.checkPermission(viewAll,
                                          user,
                                          null);
        verify(unanimousVoter).vote(any());

        VotingAlgorithm affirmativeVoter = mock(VotingAlgorithm.class);
        when(affirmativeVoter.vote(any())).thenReturn(AuthorizationResult.ACCESS_GRANTED);
        authzResultCache.clear();
        permissionManager.setDefaultVotingStrategy(VotingStrategy.AFFIRMATIVE);
        permissionManager.setVotingAlgorithm(VotingStrategy.AFFIRMATIVE,
                                             affirmativeVoter);
        permissionManager.checkPermission(viewAll,
                                          user);
        verify(affirmativeVoter).vote(any());

        VotingAlgorithm consensusVoter = mock(VotingAlgorithm.class);
        when(consensusVoter.vote(any())).thenReturn(AuthorizationResult.ACCESS_GRANTED);
        authzResultCache.clear();
        permissionManager.setDefaultVotingStrategy(VotingStrategy.CONSENSUS);
        permissionManager.setVotingAlgorithm(VotingStrategy.CONSENSUS,
                                             consensusVoter);
        permissionManager.checkPermission(viewAll,
                                          user);
        verify(consensusVoter).vote(any());
    }

    @Test
    public void testPriorityVoting1() {
        User user = createUserMock("role1",
                                   "role2",
                                   "role3");
        AuthorizationPolicy policy = permissionManager.newAuthorizationPolicy()
                .role("role1",
                      1).permission("resource.read",
                                    true)
                .role("role2",
                      2).permission("resource.read",
                                    false)
                .role("role3",
                      3).permission("resource.read.1",
                                    true)
                .build();

        permissionManager.setAuthorizationPolicy(policy);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);

        PermissionCollection pc = permissionManager.resolvePermissions(user,
                                                                       VotingStrategy.PRIORITY);
        Collection<Permission> permissions = pc.collection();
        assertEquals(permissions.size(),
                     2);
        assertTrue(permissions.contains(denyAll));
        assertTrue(permissions.contains(view1));
    }

    @Test
    public void testPriorityVoting2() {
        User user = createUserMock("role1",
                                   "role2",
                                   "role3");
        AuthorizationPolicy policy = permissionManager.newAuthorizationPolicy()
                .role("role1",
                      3).permission("resource.read",
                                    true)
                .role("role2",
                      2).permission("resource.read",
                                    false)
                .role("role3",
                      1).permission("resource.read.1",
                                    true)
                .build();

        permissionManager.setAuthorizationPolicy(policy);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_GRANTED);

        PermissionCollection pc = permissionManager.resolvePermissions(user,
                                                                       VotingStrategy.PRIORITY);
        Collection<Permission> permissions = pc.collection();
        assertEquals(permissions.size(),
                     1);
        assertTrue(permissions.contains(viewAll));
    }

    @Test
    public void testPriorityVoting3() {
        User user = createUserMock("role1",
                                   "role2",
                                   "role3");
        AuthorizationPolicy policy = permissionManager.newAuthorizationPolicy()
                .role("role1",
                      1).permission("resource.read",
                                    true)
                .role("role2",
                      2).permission("resource.read",
                                    false)
                .role("role3",
                      1).permission("resource.read.1",
                                    true)
                .build();

        permissionManager.setAuthorizationPolicy(policy);
        assertEquals(permissionManager.checkPermission(view1,
                                                       user),
                     ACCESS_DENIED);

        PermissionCollection pc = permissionManager.resolvePermissions(user,
                                                                       VotingStrategy.PRIORITY);
        Collection<Permission> permissions = pc.collection();
        assertEquals(permissions.size(),
                     1);
        assertTrue(permissions.contains(denyAll));
    }

    @Test
    public void testUnanimousVoting() {
        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy()
                                                         .role("role1").permission("resource.read",
                                                                                   true)
                                                         .role("role2").permission("resource.read",
                                                                                   false)
                                                         .role("role3").permission("resource.read",
                                                                                   true)
                                                         .build());

        User user = createUserMock("role1",
                                   "role2",
                                   "role3");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user,
                                                       VotingStrategy.UNANIMOUS),
                     ACCESS_DENIED);

        user = createUserMock("role1",
                              "role3");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user,
                                                       VotingStrategy.UNANIMOUS),
                     ACCESS_GRANTED);
    }

    @Test
    public void testConsensusVoting() {
        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy()
                                                         .role("role1").permission("resource.read",
                                                                                   true)
                                                         .role("role2").permission("resource.read",
                                                                                   false)
                                                         .role("role3").permission("resource.read",
                                                                                   true)
                                                         .build());

        User user = createUserMock("role1",
                                   "role2",
                                   "role3");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user,
                                                       VotingStrategy.CONSENSUS),
                     ACCESS_DENIED);

        user = createUserMock("role1",
                              "role3");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user,
                                                       VotingStrategy.CONSENSUS),
                     ACCESS_GRANTED);
    }

    @Test
    public void testAffirmativeVoting() {
        permissionManager.setAuthorizationPolicy(permissionManager.newAuthorizationPolicy()
                                                         .role("role1").permission("resource.read",
                                                                                   true)
                                                         .role("role2").permission("resource.read",
                                                                                   false)
                                                         .role("role3").permission("resource.read",
                                                                                   true)
                                                         .build());

        User user = createUserMock("role1",
                                   "role2",
                                   "role3");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user,
                                                       VotingStrategy.AFFIRMATIVE),
                     ACCESS_GRANTED);

        user = createUserMock("role1",
                              "role3");
        assertEquals(permissionManager.checkPermission(viewAll,
                                                       user,
                                                       VotingStrategy.AFFIRMATIVE),
                     ACCESS_GRANTED);
    }
}
