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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.enterprise.event.Event;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
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
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;
import org.uberfire.spaces.SpacesAPI;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthzPolicyDeployerTest {

    @Mock
    protected SpacesAPI spaces;

    @Mock
    AuthorizationPolicyStorage storage;

    AuthorizationPolicyVfsStorage vfsstorage;

    private FileSystem fileSystem;

    @Mock
    Event<AuthorizationPolicyDeployedEvent> event;

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    AuthorizationPolicyDeployer deployer;

    PermissionManager permissionManager;

    IOService ioService;

    @Before
    public void setUp() throws IOException {
        fileSystemTestingUtils.setup();
        fileSystem = fileSystemTestingUtils.getFileSystem();
        ioService = spy(fileSystemTestingUtils.getIoService());
        doNothing().when(ioService).startBatch(any(FileSystem.class));
        doNothing().when(ioService).endBatch();
        doReturn(fileSystem).when(ioService).newFileSystem(any(URI.class), anyMap());
        PermissionTypeRegistry permissionTypeRegistry = new DefaultPermissionTypeRegistry();
        permissionManager = spy(new DefaultPermissionManager(permissionTypeRegistry));

        vfsstorage = new AuthorizationPolicyVfsStorage(ioService, permissionManager, spaces);
        deployer = new AuthorizationPolicyDeployer(storage, permissionManager, event);
        vfsstorage.initFileSystem();
        RoleRegistry.get().clear();
    }

    @Test
    public void testPolicyDir() {
        WebAppSettings.get().setRootDir("/test");
        Path path = deployer.getPolicyDir();
        Path expected = Paths.get(URI.create("file:///test/WEB-INF/classes"));
        assertEquals(path,
                     expected);
    }

    @Test
    public void testInvalidPolicy() {
        assertThatThrownBy(() -> testPolicyLoad("WEB-INF/classes/invalid/security-policy.properties"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Key must start with [default|role|group]");
    }

    @Test
    public void testPolicyLoad() throws Exception {
        testPolicyLoad("WEB-INF/classes/security-policy.properties");
    }

    @Test
    public void testPolicyLoad2() throws Exception {
        testPolicyLoad("WEB-INF/classes/split/security-policy.properties");
    }

    @Test
    public void testPolicyDelete() throws Exception {
        testPolicyDelete("WEB-INF/classes/security-policy.properties");
    }

    public void testPolicyDelete(String path) throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource(path);
        Path policyDir = Paths.get(fileURL.toURI()).getParent();

        assertTrue(RoleRegistry.get().getRegisteredRoles().isEmpty());

        deployer.deployPolicy(policyDir);

        ArgumentCaptor<AuthorizationPolicy> policyCaptor = ArgumentCaptor.forClass(AuthorizationPolicy.class);
        verify(storage).loadPolicy();
        verify(storage).savePolicy(policyCaptor.capture());
        vfsstorage.savePolicy(policyCaptor.getValue());

        AuthorizationPolicy policy = vfsstorage.loadPolicyFromVfs();
        Set<Group> groups = policy.getGroups();
        assertEquals(1, groups.size());

        Group group = new GroupImpl("group1");
        PermissionCollection permissions = policy.getPermissions(group);
        Permission permission = permissions.get("perspective.read");
        assertNotNull(permission);
        assertEquals(AuthorizationResult.ACCESS_GRANTED, permission.getResult());

        vfsstorage.deletePolicyByGroup(group, policyCaptor.getValue());
        verify(event).fire(any());

        policy = vfsstorage.loadPolicyFromVfs();
        groups = policy.getGroups();
        assertEquals(0, groups.size());

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
        assertEquals(RoleRegistry.get().getRegisteredRoles().size(),
                     3);

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
    public void testNothingToDeploy() {
        deployer.deployPolicy(null);
        verify(storage,
               never()).loadPolicy();
        verify(storage,
               never()).savePolicy(any());
    }

    @Test
    public void testAlreadyDeployed() {
        when(storage.loadPolicy()).thenReturn(mock(AuthorizationPolicy.class));
        deployer.deployPolicy(Paths.get(""));

        verify(storage).loadPolicy();
        verify(storage,
               never()).savePolicy(any());
    }
}
