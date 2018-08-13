/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.security.server.io;

import java.net.URI;
import java.util.Arrays;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCheck;
import org.uberfire.security.authz.ResourceCheck;
import org.uberfire.security.authz.VotingStrategy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IOServiceSecuritySetupTest {

    @Test
    public void nonSecureExecuted() {
        final FileSystem fs = mock(FileSystem.class);
        final Path rootPath = mock(Path.class);

        when(fs.getRootDirectories()).thenReturn(Arrays.asList(rootPath));
        when(rootPath.getFileSystem()).thenReturn(fs);
        when(rootPath.toUri()).thenReturn(URI.create("/"));

        final IOSecurityService service = new IOSecurityService(new MockIOService(),
                                                                new MockAuthenticationService(),
                                                                new DummyAuthorizationManager(true));

        assertTrue(PriorityDisposableRegistry.getDisposables().contains(service));

        try {
            service.startBatch(fs);
        } catch (Exception e) {
            e.printStackTrace();
            fail("error");
        }
    }

    @Test
    public void secureExecuted() {
        final FileSystem fs = mock(FileSystem.class);
        final Path rootPath = mock(Path.class);
        when(rootPath.toUri()).thenReturn(URI.create("/"));

        when(fs.getRootDirectories()).thenReturn(Arrays.asList(rootPath));
        when(rootPath.getFileSystem()).thenReturn(fs);

        final IOSecurityService service = new IOSecurityService(new MockIOService(),
                                                                new MockAuthenticationService(),
                                                                new DummyAuthorizationManager(false));

        try {
            service.startBatch(fs);
            fail("error");
        } catch (SecurityException e) {
        } catch (Exception e) {
            fail("error");
        }
    }

    class DummyAuthorizationManager implements AuthorizationManager {

        private boolean grant;

        public DummyAuthorizationManager(boolean grant) {
            this.grant = grant;
        }

        @Override
        public boolean authorize(Resource resource,
                                 User user,
                                 VotingStrategy votingStrategy) {
            return grant;
        }

        @Override
        public boolean authorize(Resource resource,
                                 ResourceAction action,
                                 User user,
                                 VotingStrategy votingStrategy) {
            return grant;
        }

        @Override
        public boolean authorize(ResourceType resourceType,
                                 ResourceAction action,
                                 User user,
                                 VotingStrategy votingStrategy) {
            return grant;
        }

        @Override
        public boolean authorize(String permission,
                                 User user,
                                 VotingStrategy votingStrategy) {
            return grant;
        }

        @Override
        public boolean authorize(Permission permission,
                                 User user,
                                 VotingStrategy votingStrategy) {
            return grant;
        }

        @Override
        public ResourceCheck check(Resource resource,
                                   User user,
                                   VotingStrategy votingStrategy) {
            return null;
        }

        @Override
        public ResourceCheck check(ResourceType resourceType,
                                   User user,
                                   VotingStrategy votingStrategy) {
            return null;
        }

        @Override
        public PermissionCheck check(String permission,
                                     User user,
                                     VotingStrategy votingStrategy) {
            return null;
        }

        @Override
        public boolean authorize(Resource resource,
                                 User user) {
            return grant;
        }

        @Override
        public boolean authorize(Resource resource,
                                 ResourceAction action,
                                 User user) {
            return grant;
        }

        @Override
        public boolean authorize(ResourceType resourceType,
                                 ResourceAction action,
                                 User user) {
            return grant;
        }

        @Override
        public boolean authorize(String permission,
                                 User user) {
            return grant;
        }

        @Override
        public boolean authorize(Permission permission,
                                 User user) {
            return grant;
        }

        @Override
        public ResourceCheck check(Resource resource,
                                   User user) {
            return null;
        }

        @Override
        public ResourceCheck check(ResourceType type,
                                   User user) {
            return null;
        }

        @Override
        public PermissionCheck check(String permission,
                                     User user) {
            return null;
        }

        @Override
        public void invalidate(User user) {

        }
    }

    ;
}
