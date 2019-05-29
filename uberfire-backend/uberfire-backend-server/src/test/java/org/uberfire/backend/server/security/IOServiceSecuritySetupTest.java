/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.security;

import java.net.URI;
import java.util.Arrays;

import javax.enterprise.inject.Instance;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IOServiceSecuritySetupTest {

    @Mock
    Instance<AuthenticationService> authenticationManagers;

    AuthorizationManager authorizationManager;
    IOServiceSecuritySetup setupBean;

    @Before
    public void setup() {
        // this is the fallback configuration when no @IOSecurityAuth bean is found
        System.setProperty("org.uberfire.io.auth",
                           MockAuthenticationService.class.getName());

        PermissionManager permissionManager = new DefaultPermissionManager(new DefaultPermissionTypeRegistry());
        authorizationManager = spy(new DefaultAuthorizationManager(permissionManager));

        setupBean = new IOServiceSecuritySetup();
        setupBean.authenticationManagers = authenticationManagers;
        setupBean.authorizationManager = authorizationManager;
    }

    @After
    public void teardown() {
        System.clearProperty("org.uberfire.io.auth");
    }

    @Test
    public void testSystemPropertyAuthConfig() throws Exception {
        when(authenticationManagers.isUnsatisfied()).thenReturn(true);

        setupBean.setup();

        // setup should have initialized the authenticator and authorizer to their defaults
        MockSecuredFilesystemProvider mockFsp = MockSecuredFilesystemProvider.LATEST_INSTANCE;
        assertNotNull(mockFsp.authenticator);
        assertNotNull(mockFsp.authorizer);

        // and they should work :)
        User user = mockFsp.authenticator.login("fake", "fake");
        assertEquals(MockAuthenticationService.FAKE_USER.getIdentifier(),
                     user.getIdentifier());

        final FileSystem mockfs = mock(FileSystem.class);
        final FileSystem mockedFSId = mock(FileSystem.class,
                                           withSettings().extraInterfaces(FileSystemId.class));
        when(((FileSystemId) mockedFSId).id()).thenReturn("mockFS");
        final Path rootPath = mock(Path.class);
        when(rootPath.toUri()).thenReturn(URI.create("/"));
        when(mockfs.getRootDirectories()).thenReturn(Arrays.asList(rootPath));
        when(mockedFSId.getRootDirectories()).thenReturn(Arrays.asList(rootPath));

        when(rootPath.getFileSystem()).thenReturn(mockedFSId);

        assertTrue(mockFsp.authorizer.authorize(mockfs,
                                                user));
    }

    @Test
    public void testCustomAuthenticatorBean() throws Exception {

        // this simulates the existence of a @IOServiceAuth AuthenticationService bean
        when(authenticationManagers.isUnsatisfied()).thenReturn(false);
        AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
        when(authenticationManagers.get()).thenReturn(mockAuthenticationService);

        setupBean.setup();

        AuthenticationService authenticator = MockSecuredFilesystemProvider.LATEST_INSTANCE.authenticator;
        authenticator.login("fake", "fake");

        // make sure the call went to the one we provided
        verify(mockAuthenticationService).login("fake",
                                                "fake");
    }

    @Test
    public void testCustomAuthorizerBean() throws Exception {
        when(authenticationManagers.isUnsatisfied()).thenReturn(true);

        setupBean.setup();

        FileSystemAuthorizer installedAuthorizer = MockSecuredFilesystemProvider.LATEST_INSTANCE.authorizer;
        AuthenticationService installedAuthenticator = MockSecuredFilesystemProvider.LATEST_INSTANCE.authenticator;
        FileSystem mockfs = mock(FileSystem.class);

        final FileSystem mockedFSId = mock(FileSystem.class,
                                           withSettings().extraInterfaces(FileSystemId.class));
        final Path rootPath = mock(Path.class);
        when(rootPath.toUri()).thenReturn(URI.create("/"));
        when(mockfs.getRootDirectories()).thenReturn(Arrays.asList(rootPath));
        when(mockedFSId.getRootDirectories()).thenReturn(Arrays.asList(rootPath));
        when(rootPath.getFileSystem()).thenReturn(mockedFSId);

        User fileSystemUser = installedAuthenticator.login("fake", "fake");

        installedAuthorizer.authorize(mockfs,
                                      fileSystemUser);
        // make sure the call went to the one we provided
        verify(authorizationManager).authorize(any(FileSystemResourceAdaptor.class),
                                               any(User.class));
    }
}
