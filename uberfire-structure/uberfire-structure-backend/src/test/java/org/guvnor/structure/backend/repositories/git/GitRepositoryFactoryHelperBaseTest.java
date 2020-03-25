/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.repositories.git;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import javax.enterprise.event.Event;

import org.assertj.core.api.Assertions;
import org.guvnor.structure.backend.repositories.BranchAccessAuthorizer;
import org.guvnor.structure.backend.repositories.git.hooks.PostCommitNotificationService;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryExternalUpdateEvent;
import org.guvnor.structure.server.config.PasswordService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.extensions.FileSystemHookExecutionContext;
import org.uberfire.java.nio.file.extensions.FileSystemHooks;
import org.uberfire.java.nio.file.extensions.FileSystemHooksConstants;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

public abstract class GitRepositoryFactoryHelperBaseTest {

    enum Mode {
        INDEXED,
        NOT_INDEXED
    }

    protected Mode mode;

    @Mock
    protected IOService indexed;

    @Mock
    protected IOService notIndexed;

    @Mock
    protected FileSystem fileSystem;

    @Mock
    protected Event<RepositoryExternalUpdateEvent> repositoryExternalUpdate;

    @Mock
    protected PostCommitNotificationService postCommitNotificationService;

    @Mock
    protected BranchAccessAuthorizer branchAccessAuthorizer;

    @Mock
    private PasswordService passwordService;

    protected GitRepositoryFactoryHelper helper;

    protected ArrayList<Path> rootDirectories;
    protected SpacesAPI spacesAPI;

    public void init() {

        when(passwordService.decrypt(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0].toString());

        spacesAPI = new SpacesAPIImpl();

        helper = new GitRepositoryFactoryHelper(indexed,
                                                notIndexed,
                                                spacesAPI,
                                                repositoryExternalUpdate,
                                                postCommitNotificationService,
                                                branchAccessAuthorizer,
                                                passwordService);

        if (Mode.INDEXED.equals(mode)) {
            initServices(indexed,
                         notIndexed);
        } else {
            initServices(notIndexed,
                         indexed);
        }

        rootDirectories = new ArrayList<>();
        when(fileSystem.getRootDirectories()).thenReturn(rootDirectories);
    }

    private void initServices(IOService normal,
                              IOService withException) {
        when(normal.newFileSystem(any(URI.class),
                                  anyMap())
        ).thenReturn(fileSystem);

        when(withException.newFileSystem(any(URI.class),
                                         anyMap())).thenThrow(new RuntimeException());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSchemeConfigItem() {
        helper.newRepository(new RepositoryInfo("test",
                                                false,
                                                new RepositoryConfiguration()));
    }

    @Test(expected = IllegalStateException.class)
    public void testNotValid() {
        helper.newRepository(new RepositoryInfo(null,
                                                false,
                                                this.getConfig()));
    }

    @Test
    public void testNewRepositoryReplaceIfExists() {

        rootDirectories.add(createPath("default://master@uf-playground"));

        RepositoryConfiguration config = this.getConfig();
        config.add("replaceIfExists",
                   true);

        RepositoryInfo repositoryInfo = new RepositoryInfo("test",
                                                           false,
                                                           config);

        final IOService service = getServiceToTest();

        when(service.newFileSystem(any(URI.class),
                                   anyMap()))
                .thenThrow(FileSystemAlreadyExistsException.class)
                .thenReturn(fileSystem);
        when(service.getFileSystem(any(URI.class))).thenReturn(fileSystem);

        helper.newRepository(repositoryInfo);

        verify(passwordService).decrypt(anyString());

        verify(service,
               times(1)).delete(any(Path.class));
        verify(service,
               times(2)).newFileSystem(any(URI.class),
                                       anyMap());
    }

    @Test
    public void testFileSystemHooks() {
        rootDirectories.add(createPath("default://master@uf-playground"));

        RepositoryInfo repositoryInfo = new RepositoryInfo("test",
                                                           false,
                                                           this.getConfig());

        final IOService service = getServiceToTest();

        helper.newRepository(repositoryInfo);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

        verify(service).newFileSystem(any(URI.class),
                                      captor.capture());

        Map params = captor.getValue();

        Assertions.assertThat(params)
                .isNotNull();

        Assertions.assertThat(params.get(FileSystemHooks.ExternalUpdate.name()))
                .isNotNull()
                .isInstanceOf(FileSystemHooks.FileSystemHook.class);

        FileSystemHookExecutionContext ctx = new FileSystemHookExecutionContext("test");

        FileSystemHooks.FileSystemHook hook = (FileSystemHooks.FileSystemHook) params.get(FileSystemHooks.ExternalUpdate.name());
        hook.execute(ctx);
        verify(repositoryExternalUpdate).fire(any());

        Assertions.assertThat(params.get(FileSystemHooks.PostCommit.name()))
                .isNotNull()
                .isInstanceOf(FileSystemHooks.FileSystemHook.class);

        ctx.addParam(FileSystemHooksConstants.POST_COMMIT_EXIT_CODE,
                     0);

        hook = (FileSystemHooks.FileSystemHook) params.get(FileSystemHooks.PostCommit.name());
        hook.execute(ctx);
        verify(postCommitNotificationService).notifyUser(any(),
                                                         eq(0));
    }

    private IOService getServiceToTest() {
        if (Mode.INDEXED.equals(mode)) {
            return indexed;
        } else {
            return notIndexed;
        }
    }

    @Test
    public void testBranches() {

        rootDirectories.add(createPath("default://origin@uf-playground"));
        rootDirectories.add(createPath("default://master@uf-playground"));
        rootDirectories.add(createPath("default://branch1@uf-playground"));

        RepositoryInfo repositoryInfo = new RepositoryInfo("test",
                                                           false,
                                                           this.getConfig());

        Repository repository = helper.newRepository(repositoryInfo);

        assertEquals(3,
                     repository.getBranches().size());
        assertTrue(repository.getDefaultBranch().get().getPath().toURI().contains("master"));
    }

    @Test
    public void testCredentialsAreNotStoredAfterRepositoryCreation() {
        final RepositoryInfo repositoryInfo = new RepositoryInfo("myRepository",
                                                                 false,
                                                                 this.getConfig());

        final Repository repository = helper.newRepository(repositoryInfo);

        assertFalse(repository.getEnvironment().containsKey(EnvironmentParameters.USER_NAME));
        assertFalse(repository.getEnvironment().containsKey(EnvironmentParameters.PASSWORD));
        assertFalse(repository.getEnvironment()
                            .containsKey(EnvironmentParameters.SECURE_PREFIX + EnvironmentParameters.PASSWORD));
    }

    protected Path createPath(String uri) {
        Path path = mock(Path.class);
        when(path.toUri()).thenReturn(URI.create(uri));
        when(path.getFileSystem()).thenReturn(fileSystem);
        return path;
    }

    protected RepositoryConfiguration getConfig() {
        RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();
        repositoryConfiguration.add(EnvironmentParameters.USER_NAME, "user");
        repositoryConfiguration.add(EnvironmentParameters.PASSWORD, "pw");
        repositoryConfiguration.add(EnvironmentParameters.SECURE_PREFIX + EnvironmentParameters.PASSWORD, "pass");
        repositoryConfiguration.add(EnvironmentParameters.SCHEME, "git");
        repositoryConfiguration.add(EnvironmentParameters.SPACE, "space");

        return repositoryConfiguration;
    }
}
