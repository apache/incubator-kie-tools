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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.sshd.server.SshServer;
import org.assertj.core.api.Assertions;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.extensions.FileSystemHookExecutionContext;
import org.uberfire.java.nio.file.extensions.FileSystemHooks;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.GIT_DAEMON_PORT;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.GIT_SSH_IDLE_TIMEOUT;
import static org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration.GIT_SSH_PORT;

public class JGitFileSystemImplProviderSSHTest extends AbstractTestInfra {

    private int gitSSHPort;
    private int gitPort;

    @Override
    public Map<String, String> getGitPreferences() {
        final Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put(GIT_DAEMON_ENABLED, "true");
        gitPort = findFreePort();
        gitPrefs.put(GIT_DAEMON_PORT, String.valueOf(gitPort));
        gitPrefs.put(GIT_SSH_ENABLED, "true");
        gitSSHPort = findFreePort();
        gitPrefs.put(GIT_SSH_PORT, String.valueOf(gitSSHPort));
        gitPrefs.put(GIT_SSH_IDLE_TIMEOUT, "10001");

        return gitPrefs;
    }

    @Test
    public void testSSHPostReceiveHook() throws IOException {
        FileSystemHooks.FileSystemHook hook = spy(new FileSystemHooks.FileSystemHook() {
            @Override
            public void execute(FileSystemHookExecutionContext context) {
                assertEquals("repo", context.getFsName());
            }
        });

        Assume.assumeFalse("UF-511",
                           System.getProperty("java.vendor").equals("IBM Corporation"));
        //Setup Authorization/Authentication
        provider.setJAASAuthenticator(new AuthenticationService() {
            private User user;

            @Override
            public User login(String s, String s1) {
                user = new UserImpl(s);
                return user;
            }

            @Override
            public boolean isLoggedIn() {
                return user != null;
            }

            @Override
            public void logout() {
                user = null;
            }

            @Override
            public User getUser() {
                return user;
            }
        });
        provider.setAuthorizer((fs, fileSystemUser) -> true);

        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider("admin",
                                                                               ""));
        assertEquals("10001",
                     provider.getGitSSHService().getProperties().get(SshServer.IDLE_TIMEOUT));

        //Setup origin
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem(originRepo,
                                                                              new HashMap<String, Object>() {{
                                                                                  put(FileSystemHooks.ExternalUpdate.name(), hook);
                                                                              }});

        //Write a file to origin that we won't amend in the clone
        new Commit(origin.getGit(),
                   "master",
                   "user1",
                   "user1@example.com",
                   "commitx",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file-name.txt",
                           tempFile("temp1"));
                   }}).execute();

        //Setup clone
        JGitFileSystem clone;
        clone = (JGitFileSystem) provider.newFileSystem(URI.create("git://repo-clone"),
                                                        new HashMap<String, Object>() {{
                                                            put("init",
                                                                "true");
                                                            put("origin",
                                                                "ssh://admin@localhost:" + gitSSHPort + "/repo");
                                                        }});

        assertNotNull(clone);

        //Push clone back to origin
        provider.getFileSystem(URI.create("git://repo-clone?push=ssh://admin@localhost:" + gitSSHPort + "/repo"));

        ArgumentCaptor<FileSystemHookExecutionContext> captor = ArgumentCaptor.forClass(FileSystemHookExecutionContext.class);

        verify(hook).execute(captor.capture());

        Assertions.assertThat(captor.getValue())
                .isNotNull()
                .hasFieldOrPropertyWithValue("fsName", "repo");
    }

    @Test
    public void testGitProtocolReadOnly() throws IOException {
        //Setup origin
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem(originRepo,
                                                                              new HashMap<String, Object>() {{
                                                                                  put("init", "true");
                                                                              }});

        //Write a file to origin that we won't amend in the clone
        new Commit(origin.getGit(),
                   "master",
                   "user1",
                   "user1@example.com",
                   "commitx",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file-name.txt",
                           tempFile("temp1"));
                   }}).execute();

        //Setup clone
        final JGitFileSystem clone = (JGitFileSystem) provider.newFileSystem(URI.create("git://repo-clone"),
                                                                             new HashMap<String, Object>() {{
                                                                                 put("init",
                                                                                     "true");
                                                                                 put("origin",
                                                                                     "git://localhost:" + gitPort + "/repo");
                                                                             }});

        Files.write(clone.getPath("/home/file.txt"), "test".getBytes());

        try {
            provider.getFileSystem(URI.create("git://repo-clone?push=git://localhost:" + gitPort + "/repo"));
            fail("should fail");
        } catch (Throwable ex) {
        }

        try {
            provider.getFileSystem(URI.create("git://repo-clone?ssh=git://localhost:" + gitSSHPort + "/repo"));
        } catch (Throwable ex) {
            fail("should not fail");
        }

        assertNotNull(clone);
    }
}
