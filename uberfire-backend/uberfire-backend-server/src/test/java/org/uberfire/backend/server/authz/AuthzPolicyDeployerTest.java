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

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.enterprise.event.Event;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.events.AuthorizationPolicyDeployedEvent;
import org.uberfire.backend.server.WebAppSettings;
import org.uberfire.backend.server.security.RoleRegistry;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthzPolicyDeployerTest {

    @Mock
    AuthorizationPolicyStorage storage;

    @Mock
    Event<AuthorizationPolicyDeployedEvent> event;

    AuthorizationPolicyDeployer deployer;
    PermissionManager permissionManager;

    @Before
    public void setUp() {
        PermissionTypeRegistry permissionTypeRegistry = new DefaultPermissionTypeRegistry();
        permissionManager = spy(new DefaultPermissionManager(permissionTypeRegistry));
        deployer = new AuthorizationPolicyDeployer(storage, permissionManager, event);
        RoleRegistry.get().clear();
    }

    @Test
    public void testPolicyDir() throws Exception {
        WebAppSettings.get().setRootDir("/test");
        Path path = deployer.getPolicyDir();
        Path expected = Paths.get(URI.create("file:///test/WEB-INF/classes"));
        assertEquals(path, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPolicy() throws Exception {
        testPolicyLoad("WEB-INF/classes/invalid/security-policy.properties");
    }

    @Test
    public void testPolicyLoad() throws Exception {
        testPolicyLoad("WEB-INF/classes/security-policy.properties");
    }

    @Test
    public void testPolicyLoad2() throws Exception {
        testPolicyLoad("WEB-INF/classes/split/security-policy.properties");
    }

    public void testPolicyLoad(String path) throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource(path);
        Path policyDir = Paths.get(fileURL.toURI()).getParent();

        assertTrue(RoleRegistry.get().getRegisteredRoles().isEmpty());

        deployer.deployPolicy(policyDir);

        ArgumentCaptor<AuthorizationPolicy> policyCaptor = ArgumentCaptor.forClass(AuthorizationPolicy.class);
        verify(storage).loadPolicy();
        verify(storage).savePolicy(policyCaptor.capture());
        verify(event).fire(any());

        AuthorizationPolicy policy = policyCaptor.getValue();
        verify(permissionManager).setAuthorizationPolicy(policy);
        assertEquals(RoleRegistry.get().getRegisteredRoles().size(), 3);

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
    public void testNothingToDeploy() throws Exception {
        deployer.deployPolicy(null);
        verify(storage, never()).loadPolicy();
        verify(storage, never()).savePolicy(any());
    }

    @Test
    public void testAlreadyDeployed() throws Exception {
        when(storage.loadPolicy()).thenReturn(mock(AuthorizationPolicy.class));
        deployer.deployPolicy(Paths.get(""));

        verify(storage).loadPolicy();
        verify(storage, never()).savePolicy(any());
    }
}
