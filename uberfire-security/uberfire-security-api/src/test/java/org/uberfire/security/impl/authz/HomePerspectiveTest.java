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
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HomePerspectiveTest {

    PermissionManager permissionManager;
    AuthorizationPolicy authorizationPolicy;

    protected User createUserMock(String... roles) {
        User user = mock(User.class);
        Set<Role> roleSet = Stream.of(roles).map(RoleImpl::new).collect(Collectors.toSet());
        when(user.getRoles()).thenReturn(roleSet);
        when(user.getGroups()).thenReturn(null);
        return user;
    }

    @Before
    public void setUp() {
        permissionManager = new DefaultPermissionManager(new DefaultPermissionTypeRegistry());
        permissionManager.setAuthorizationPolicy(
                authorizationPolicy = spy(permissionManager.newAuthorizationPolicy()
                                                  .bydefault().home("H")
                                                  .role("admin").home("A").priority(10)
                                                  .role("user").home("U").priority(0)
                                                  .role("manager").home("M").priority(5)
                                                  .build()));
    }

    @Test
    public void testUserWithoutRoles() {
        User userMock = createUserMock();
        String home = authorizationPolicy.getHomePerspective(userMock);
        assertEquals(home,
                     "H");
    }

    @Test
    public void testSingleRoleHome() {
        User userMock = createUserMock("admin");
        String home = authorizationPolicy.getHomePerspective(userMock);
        assertEquals(home,
                     "A");

        userMock = createUserMock("manager");
        home = authorizationPolicy.getHomePerspective(userMock);
        assertEquals(home,
                     "M");

        userMock = createUserMock("user");
        home = authorizationPolicy.getHomePerspective(userMock);
        assertEquals(home,
                     "U");
    }

    @Test
    public void testHighestPriorityWins() {
        User userMock = createUserMock("admin",
                                       "user");
        String home = authorizationPolicy.getHomePerspective(userMock);
        assertEquals(home,
                     "A");

        userMock = createUserMock("admin",
                                  "user",
                                  "manager");
        home = authorizationPolicy.getHomePerspective(userMock);
        assertEquals(home,
                     "A");

        userMock = createUserMock("user",
                                  "manager");
        home = authorizationPolicy.getHomePerspective(userMock);
        assertEquals(home,
                     "M");
    }
}
