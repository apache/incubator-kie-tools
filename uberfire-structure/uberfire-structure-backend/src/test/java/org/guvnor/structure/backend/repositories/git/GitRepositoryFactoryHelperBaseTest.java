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

import org.assertj.core.api.Assertions;
import org.guvnor.structure.backend.repositories.BranchAccessAuthorizer;
import org.guvnor.structure.backend.repositories.git.hooks.PostCommitNotificationService;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryExternalUpdateEvent;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
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

import javax.enterprise.event.Event;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

public abstract class GitRepositoryFactoryHelperBaseTest {

    enum Mode {
        INDEXED, NOT_INDEXED;
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

    protected GitRepositoryFactoryHelper helper;

    protected ArrayList<Path> rootDirectories;
    protected SpacesAPI spacesAPI;


    public void init() {

        spacesAPI = new SpacesAPIImpl();

        helper = new GitRepositoryFactoryHelper(indexed,
                notIndexed,
                spacesAPI,
                repositoryExternalUpdate,
                postCommitNotificationService,
                branchAccessAuthorizer);



        if(Mode.INDEXED.equals(mode)) {
            initServices(indexed, notIndexed);
        } else {
            initServices(notIndexed, indexed);
        }

        rootDirectories = new ArrayList<Path>();
        when(fileSystem.getRootDirectories()).thenReturn(rootDirectories);
    }

    private void initServices(IOService normal, IOService withException) {
        when(normal.newFileSystem(any(URI.class), anyMap())
        ).thenReturn(fileSystem);

        when(withException.newFileSystem(any(URI.class), anyMap())).thenThrow(new RuntimeException());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSchemeConfigItem() throws Exception {
        helper.newRepository(new ConfigGroup());
    }

    @Test(expected = IllegalStateException.class)
    public void testNotValid() throws Exception {
        helper.newRepository(getConfigGroup());
    }

    @Test
    public void testNewRepositoryDontReplaceIfExists() {

        rootDirectories.add(createPath("default://master@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        ConfigItem configItem = new ConfigItem();
        configItem.setName("replaceIfExists");
        configItem.setValue(false);
        configGroup.setConfigItem(configItem);

        final IOService service = getServiceToTest();

        when(service.newFileSystem(any(URI.class),
                anyMap()))
                .thenThrow(FileSystemAlreadyExistsException.class);
        when(service.getFileSystem(any(URI.class))).thenReturn(fileSystem);

        helper.newRepository(configGroup);

        verify(service,
                never()).delete(any(Path.class));
        verify(service,
                times(1)).newFileSystem(any(URI.class),
                anyMap());
    }

    @Test
    public void testNewRepositoryReplaceIfExists() {

        rootDirectories.add(createPath("default://master@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        ConfigItem configItem = new ConfigItem();
        configItem.setName("replaceIfExists");
        configItem.setValue(true);
        configGroup.setConfigItem(configItem);

        final IOService service = getServiceToTest();

        when(service.newFileSystem(any(URI.class),
                anyMap()))
                .thenThrow(FileSystemAlreadyExistsException.class)
                .thenReturn(fileSystem);
        when(service.getFileSystem(any(URI.class))).thenReturn(fileSystem);

        helper.newRepository(configGroup);

        verify(service, times(1)).delete(any(Path.class));
        verify(service, times(2)).newFileSystem(any(URI.class), anyMap());
    }

    @Test
    public void testFileSystemHooks() {
        rootDirectories.add(createPath("default://master@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        final IOService service = getServiceToTest();

        helper.newRepository(configGroup);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

        verify(service).newFileSystem(any(URI.class), captor.capture());

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

        ctx.addParam(FileSystemHooksConstants.POST_COMMIT_EXIT_CODE, 0);

        hook = (FileSystemHooks.FileSystemHook) params.get(FileSystemHooks.PostCommit.name());
        hook.execute(ctx);
        verify(postCommitNotificationService).notifyUser(any(), eq(0));
    }

    private IOService getServiceToTest() {
        if(Mode.INDEXED.equals(mode)) {
            return indexed;
        } else {
            return notIndexed;
        }
    }

    @Test
    public void testBranches() throws Exception {

        rootDirectories.add(createPath("default://origin@uf-playground"));
        rootDirectories.add(createPath("default://master@uf-playground"));
        rootDirectories.add(createPath("default://branch1@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        Repository repository = helper.newRepository(configGroup);

        assertEquals(3,
                repository.getBranches().size());
        assertTrue(repository.getDefaultBranch().get().getPath().toURI().contains("master"));
    }

    protected Path createPath(String uri) {
        Path path = mock(Path.class);
        when(path.toUri()).thenReturn(URI.create(uri));
        when(path.getFileSystem()).thenReturn(fileSystem);
        return path;
    }

    protected ConfigGroup getConfigGroup() {
        ConfigGroup repoConfig = new ConfigGroup();
        {
            ConfigItem configItem = new ConfigItem();
            configItem.setName(EnvironmentParameters.SCHEME);
            repoConfig.addConfigItem(configItem);
        }
        {
            ConfigItem configItem = new ConfigItem();
            configItem.setName(EnvironmentParameters.SPACE);
            configItem.setValue("space");
            repoConfig.addConfigItem(configItem);
        }

        return repoConfig;
    }
}
