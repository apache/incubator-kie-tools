/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.VotingStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPermissionManagerTest {

    private static final String PERMISSION_NAME = "guideddecisiontable.edit.columns";

    private DefaultAuthorizationPolicy authorizationPolicy;

    private DefaultPermissionManager defaultPermissionManager;

    private DefaultPermissionTypeRegistry permissionTypeRegistry;

    private DefaultAuthzResultCache cache;

    @Before
    public void setUp() {
        permissionTypeRegistry = spy(new DefaultPermissionTypeRegistry());
        cache = spy(new DefaultAuthzResultCache());
        defaultPermissionManager = spy(new DefaultPermissionManager(permissionTypeRegistry, cache));
        authorizationPolicy = spy(new DefaultAuthorizationPolicy());

        defaultPermissionManager.setAuthorizationPolicy(authorizationPolicy);
    }

    @Test
    public void testResolvePermissionsCustomDeniedOverDefaultGranted() {

        final VotingStrategy priority = VotingStrategy.PRIORITY;
        final Role businessUserRole = new RoleImpl("business-user");
        final Group directorGroup = new GroupImpl("director");

        // Users have a group with their names by default
        final User user = makeUser("director", directorGroup, businessUserRole);

        mockDefaultPermissions(authorizationPolicy,
                               makeGrantedPermissionCollection());
        mockRolePermissions(authorizationPolicy,
                            makeDeniedPermissionCollection(),
                            businessUserRole,
                            0);
        mockDefaultGroupPermissions(authorizationPolicy,
                                    makeGrantedPermissionCollection(),
                                    directorGroup);

        final PermissionCollection resolvedPermission = defaultPermissionManager.resolvePermissions(user, priority);
        final Permission permission = resolvedPermission.get(PERMISSION_NAME);

        assertEquals(AuthorizationResult.ACCESS_DENIED, permission.getResult());
    }

    @Test
    public void testResolvePermissionsCustomGrantedOverDefaultDenied() {

        final VotingStrategy priority = VotingStrategy.PRIORITY;
        final Role businessUserRole = new RoleImpl("business-user");
        final Group directorGroup = new GroupImpl("director");

        // Users have a group with their names by default
        final User user = makeUser("director", directorGroup, businessUserRole);

        mockDefaultPermissions(authorizationPolicy,
                               makeDeniedPermissionCollection());
        mockRolePermissions(authorizationPolicy,
                            makeGrantedPermissionCollection(),
                            businessUserRole,
                            0);
        mockDefaultGroupPermissions(authorizationPolicy,
                                    makeDeniedPermissionCollection(),
                                    directorGroup);

        final PermissionCollection resolvedPermission = defaultPermissionManager.resolvePermissions(user, priority);
        final Permission permission = resolvedPermission.get(PERMISSION_NAME);

        assertEquals(AuthorizationResult.ACCESS_GRANTED, permission.getResult());
    }

    @Test
    public void testResolvePermissionsTwoCustomRolesGranted() {

        final VotingStrategy priority = VotingStrategy.PRIORITY;
        final Role businessUserRole = new RoleImpl("business-user");
        final Role managerRole = new RoleImpl("manager");
        final Group directorGroup = new GroupImpl("director");

        // Users have a group with their names by default
        final User user = makeUser("director", directorGroup, businessUserRole, managerRole);

        mockDefaultPermissions(authorizationPolicy,
                               makeDeniedPermissionCollection());
        mockRolePermissions(authorizationPolicy,
                            makeDeniedPermissionCollection(),
                            businessUserRole,
                            0);
        mockRolePermissions(authorizationPolicy,
                            makeGrantedPermissionCollection(),
                            managerRole,
                            1);
        mockDefaultGroupPermissions(authorizationPolicy,
                                    makeDeniedPermissionCollection(),
                                    directorGroup);

        final PermissionCollection resolvedPermission = defaultPermissionManager.resolvePermissions(user, priority);
        final Permission permission = resolvedPermission.get(PERMISSION_NAME);

        assertEquals(AuthorizationResult.ACCESS_GRANTED, permission.getResult());
    }

    @Test
    public void testResolvePermissionsTwoCustomRolesDenied() {

        final VotingStrategy priority = VotingStrategy.PRIORITY;
        final Role businessUserRole = new RoleImpl("business-user");
        final Role managerRole = new RoleImpl("manager");
        final Group directorGroup = new GroupImpl("director");

        // Users have a group with their names by default
        final User user = makeUser("director", directorGroup, businessUserRole, managerRole);

        mockDefaultPermissions(authorizationPolicy,
                               makeDeniedPermissionCollection());
        mockRolePermissions(authorizationPolicy,
                            makeDeniedPermissionCollection(),
                            businessUserRole,
                            1);
        mockRolePermissions(authorizationPolicy,
                            makeGrantedPermissionCollection(),
                            managerRole,
                            0);
        mockDefaultGroupPermissions(authorizationPolicy,
                                    makeDeniedPermissionCollection(),
                                    directorGroup);

        final PermissionCollection resolvedPermission = defaultPermissionManager.resolvePermissions(user, priority);
        final Permission permission = resolvedPermission.get(PERMISSION_NAME);

        assertEquals(AuthorizationResult.ACCESS_DENIED, permission.getResult());
    }

    @Test
    public void testResolvePermissionsTwoCustomRolesSamePriority() {

        final VotingStrategy priority = VotingStrategy.PRIORITY;
        final Role businessUserRole = new RoleImpl("business-user");
        final Role managerRole = new RoleImpl("manager");
        final Group directorGroup = new GroupImpl("director");

        // Users have a group with their names by default
        final User user = makeUser("director", directorGroup, businessUserRole, managerRole);

        mockDefaultPermissions(authorizationPolicy,
                               makeDeniedPermissionCollection());
        mockRolePermissions(authorizationPolicy,
                            makeDeniedPermissionCollection(),
                            businessUserRole,
                            0);
        mockRolePermissions(authorizationPolicy,
                            makeGrantedPermissionCollection(),
                            managerRole,
                            0);
        mockDefaultGroupPermissions(authorizationPolicy,
                                    makeDeniedPermissionCollection(),
                                    directorGroup);

        final PermissionCollection resolvedPermission = defaultPermissionManager.resolvePermissions(user, priority);
        final Permission permission = resolvedPermission.get(PERMISSION_NAME);

        assertEquals(AuthorizationResult.ACCESS_GRANTED, permission.getResult());
    }

    @Test
    public void testPermissionCache() {

        final VotingStrategy priority = VotingStrategy.PRIORITY;
        final Role businessUserRole = new RoleImpl("business-user");
        final Role managerRole = new RoleImpl("manager");
        final Group directorGroup = new GroupImpl("director");

        // Users have a group with their names by default
        final User user = makeUser("director", directorGroup, businessUserRole, managerRole);

        mockDefaultPermissions(authorizationPolicy,
                               makeDeniedPermissionCollection());
        mockRolePermissions(authorizationPolicy,
                            makeDeniedPermissionCollection(),
                            businessUserRole,
                            0);
        mockRolePermissions(authorizationPolicy,
                            makeGrantedPermissionCollection(),
                            managerRole,
                            0);
        mockDefaultGroupPermissions(authorizationPolicy,
                                    makeDeniedPermissionCollection(),
                                    directorGroup);

        assertNull(cache.get(user, makePermissionGranted()));
        final AuthorizationResult resolvedGrantedPermission = defaultPermissionManager.checkPermission(makePermissionGranted(), user, priority);
        verify(defaultPermissionManager).resolvePermissions(user, priority);
        assertEquals(AuthorizationResult.ACCESS_GRANTED, resolvedGrantedPermission);
        assertEquals(AuthorizationResult.ACCESS_GRANTED, cache.get(user, makePermissionGranted()));

        assertNull(cache.get(user, makePermissionDenied()));
        final AuthorizationResult resolvedDeniedPermission = defaultPermissionManager.checkPermission(makePermissionDenied(), user, priority);
        verify(defaultPermissionManager, times(2)).resolvePermissions(user, priority);
        assertEquals(AuthorizationResult.ACCESS_DENIED, resolvedDeniedPermission);
        assertEquals(AuthorizationResult.ACCESS_DENIED, cache.get(user, makePermissionDenied()));

        assertEquals(2, cache.size(user));
    }

    private void mockDefaultGroupPermissions(final DefaultAuthorizationPolicy authorizationPolicy,
                                             final DefaultPermissionCollection permissionCollection,
                                             final Group group) {

        final DefaultAuthorizationEntry groupAuthorizationEntry = new DefaultAuthorizationEntry() {{
            setGroup(group);
            // Simulating a priority with the default value
        }};

        authorizationPolicy.registerAuthzEntry(groupAuthorizationEntry);

        doReturn(permissionCollection).when(authorizationPolicy).getPermissions(group);
    }

    private void mockRolePermissions(final DefaultAuthorizationPolicy authorizationPolicy,
                                     final DefaultPermissionCollection permissionCollection,
                                     final Role role,
                                     final int priority) {

        final DefaultAuthorizationEntry roleAuthorizationEntry = new DefaultAuthorizationEntry() {{
            setRole(role);

            // Simulating a priority set by the user
            setPriority(priority);
        }};

        authorizationPolicy.registerAuthzEntry(roleAuthorizationEntry);

        doReturn(permissionCollection).when(authorizationPolicy).getPermissions(role);
    }

    private void mockDefaultPermissions(final DefaultAuthorizationPolicy authorizationPolicy,
                                        final DefaultPermissionCollection permissionCollection) {
        doReturn(permissionCollection).when(authorizationPolicy).getPermissions();
    }

    private UserImpl makeUser(final String name,
                              final Group group,
                              final Role... roles) {

        return new UserImpl(name, Arrays.asList(roles), Collections.singletonList(group));
    }

    private DefaultPermissionCollection makeDeniedPermissionCollection() {
        return new DefaultPermissionCollection() {{
            add(makePermissionDenied());
        }};
    }

    private DefaultPermissionCollection makeGrantedPermissionCollection() {
        return new DefaultPermissionCollection() {{
            add(makePermissionGranted());
        }};
    }

    private DotNamedPermission makePermissionDenied() {
        return new DotNamedPermission(PERMISSION_NAME, false);
    }

    private DotNamedPermission makePermissionGranted() {
        return new DotNamedPermission(PERMISSION_NAME, true);
    }
}
