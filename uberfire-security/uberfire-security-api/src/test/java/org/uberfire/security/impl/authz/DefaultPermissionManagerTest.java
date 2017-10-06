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

import java.util.HashSet;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPermissionManagerTest {

    private static final String PERMISSION_NAME = "guideddecisiontable.edit.columns";

    private DefaultAuthorizationPolicy authorizationPolicy;

    private DefaultPermissionManager defaultPermissionManager;

    @Before
    public void setUp() {
        defaultPermissionManager = spy(new DefaultPermissionManager());
        authorizationPolicy = spy(new DefaultAuthorizationPolicy());

        defaultPermissionManager.setAuthorizationPolicy(authorizationPolicy);
    }

    @Test
    public void testResolvePermissionsPriority() {

        final VotingStrategy priority = VotingStrategy.PRIORITY;
        final User user = makeUser("director", new RoleImpl("business-user"));

        mockAuthorizationPolicy(user);

        final PermissionCollection resolvedPermission = defaultPermissionManager.resolvePermissions(user, priority);
        final Permission permission = resolvedPermission.get(PERMISSION_NAME);

        assertEquals(AuthorizationResult.ACCESS_DENIED, permission.getResult());
    }

    private void mockAuthorizationPolicy(final User user) {
        mockDefaultPermissions(authorizationPolicy);
        mockRolePermissions(authorizationPolicy, user);
        mockGroupPermissions(authorizationPolicy, user);
    }

    private void mockGroupPermissions(final DefaultAuthorizationPolicy authorizationPolicy,
                                      final User user) {

        final Group group = user.getGroups().iterator().next();
        final DefaultAuthorizationEntry groupAuthorizationEntry = new DefaultAuthorizationEntry() {{
            setGroup(group);
            // Simulating a priority with the default value
        }};

        authorizationPolicy.registerAuthzEntry(groupAuthorizationEntry);

        doReturn(makeGrantedPermission()).when(authorizationPolicy).getPermissions(group);
    }

    private void mockRolePermissions(final DefaultAuthorizationPolicy authorizationPolicy,
                                     final User user) {

        final Role role = user.getRoles().iterator().next();
        final DefaultAuthorizationEntry roleAuthorizationEntry = new DefaultAuthorizationEntry() {{
            setRole(role);

            // Simulating a priority set by the user
            setPriority(0);
        }};

        authorizationPolicy.registerAuthzEntry(roleAuthorizationEntry);

        doReturn(makeDeniedPermissionCollection()).when(authorizationPolicy).getPermissions(role);
    }

    private void mockDefaultPermissions(final DefaultAuthorizationPolicy authorizationPolicy) {
        doReturn(makeGrantedPermission()).when(authorizationPolicy).getPermissions();
    }

    private UserImpl makeUser(final String name,
                              final Role role) {

        final HashSet<Role> roles = new HashSet<Role>() {{
            add(role);
        }};
        final HashSet<Group> groups = new HashSet<Group>() {{
            // Users have a group with their names by default
            add(new GroupImpl(name));
        }};

        return new UserImpl(name, roles, groups);
    }

    private DefaultPermissionCollection makeDeniedPermissionCollection() {
        return new DefaultPermissionCollection() {{
            add(makePermissionDenied());
        }};
    }

    private DefaultPermissionCollection makeGrantedPermission() {
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
