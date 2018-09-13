/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories.git;

import java.net.URI;
import java.util.ArrayList;

import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

public class GitRepositoryFactoryHelperTest {

    private IOService ioService;
    private IOService notIndexed;
    private GitRepositoryFactoryHelper helper;
    private FileSystem fileSystem;
    private ArrayList<Path> rootDirectories;
    private SpacesAPI spacesAPI;

    @Before
    public void setUp() throws Exception {
        ioService = mock(IOService.class);
        notIndexed = mock(IOService.class);
        spacesAPI = new SpacesAPIImpl();
        helper = new GitRepositoryFactoryHelper(ioService,
                                                notIndexed,
                                                spacesAPI,
                                                null);

        fileSystem = mock(FileSystem.class);
        when(
                ioService.newFileSystem(any(URI.class),
                                        anyMap())
        ).thenReturn(
                fileSystem
        );

        when(
                notIndexed.newFileSystem(any(URI.class),
                                         anyMap())
        ).thenThrow(new RuntimeException());

        rootDirectories = new ArrayList<Path>();
        when(fileSystem.getRootDirectories()).thenReturn(rootDirectories);
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
    public void testNewRepositoryDontReplaceIfExists() throws Exception {

        rootDirectories.add(createPath("default://master@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        ConfigItem configItem = new ConfigItem();
        configItem.setName("replaceIfExists");
        configItem.setValue(false);
        configGroup.setConfigItem(configItem);


        when(ioService.newFileSystem(any(URI.class),
                                     anyMap()))
                .thenThrow(FileSystemAlreadyExistsException.class);
        when(ioService.getFileSystem(any(URI.class))).thenReturn(fileSystem);

        helper.newRepository(configGroup);

        verify(ioService,
               never()).delete(any(Path.class));
        verify(ioService,
               times(1)).newFileSystem(any(URI.class),
                                       anyMap());
    }

    @Test
    public void testNewRepositoryReplaceIfExists() throws Exception {

        rootDirectories.add(createPath("default://master@uf-playground"));

        ConfigGroup configGroup = getConfigGroup();
        configGroup.setName("test");

        ConfigItem configItem = new ConfigItem();
        configItem.setName("replaceIfExists");
        configItem.setValue(true);
        configGroup.setConfigItem(configItem);

        configItem = new ConfigItem();
        configItem.setName(EnvironmentParameters.SPACE);
        configItem.setValue("space");
        configGroup.addConfigItem(configItem);

        when(ioService.newFileSystem(any(URI.class),
                                     anyMap()))
                .thenThrow(FileSystemAlreadyExistsException.class)
                .thenReturn(fileSystem);
        when(ioService.getFileSystem(any(URI.class))).thenReturn(fileSystem);

        helper.newRepository(configGroup);

        verify(ioService,
               times(1)).delete(any(Path.class));
        verify(ioService,
               times(2)).newFileSystem(any(URI.class),
                                       anyMap());
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

    private Path createPath(String uri) {
        Path path = mock(Path.class);
        when(path.toUri()).thenReturn(URI.create(uri));
        when(path.getFileSystem()).thenReturn(fileSystem);
        return path;
    }

    private ConfigGroup getConfigGroup() {
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
