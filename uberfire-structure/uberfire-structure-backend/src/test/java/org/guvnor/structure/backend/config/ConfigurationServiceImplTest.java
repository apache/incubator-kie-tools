/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.config;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceImplTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Mock
    private org.guvnor.structure.repositories.Repository systemRepository;

    @Mock
    private User identity;

    @Mock
    private Event<SystemRepositoryChangedEvent> repoChangedEvent;

    @Mock
    private Event<SystemRepositoryChangedEvent> spaceChangedEvent;

    @Mock
    private Event<SystemRepositoryChangedEvent> changedEvent;

    private ConfigGroupMarshaller marshaller;

    private IOService ioService;

    private ConfigurationFactory configurationFactory;

    private ConfigurationService configurationService;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();
        when(systemRepository.getUri()).thenReturn("git://amend-repo-test");

        marshaller = new ConfigGroupMarshaller();
        configurationFactory = new ConfigurationFactoryImpl(new DefaultPasswordServiceImpl());
        ioService = mockIoService();
        configurationService = new ConfigurationServiceImpl(systemRepository,
                                                            marshaller,
                                                            identity,
                                                            ioService,
                                                            repoChangedEvent,
                                                            spaceChangedEvent,
                                                            changedEvent,
                                                            fileSystemTestingUtils.getFileSystem());
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void addAndGetConfigurationWithoutNamespaceTest() {
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                                  "global1",
                                                                                  "global1-description"));
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.SPACE,
                                                                                  "space1",
                                                                                  "space1-description"));
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                                  "global2",
                                                                                  "global2-description"));

        final List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration(ConfigType.GLOBAL);
        assertEquals(2,
                     globalConfigGroups.size());
        assertEquals("global1",
                     globalConfigGroups.get(0).getName());
        assertEquals("global2",
                     globalConfigGroups.get(1).getName());

        final List<ConfigGroup> spaceConfigGroups = configurationService.getConfiguration(ConfigType.SPACE);
        assertEquals(1,
                     spaceConfigGroups.size());
        assertEquals("space1",
                     spaceConfigGroups.get(0).getName());
    }

    @Test(expected = RuntimeException.class)
    public void addAndGetConfigurationWithoutNamespaceButTypeRequiresNamespaceTest() {
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                                  "namespace1",
                                                                                  "global1",
                                                                                  "global1-description"));

        configurationService.getConfiguration(ConfigType.REPOSITORY);
    }

    @Test
    public void addAndGetConfigurationWithNamespaceTest() {
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                                  "namespace1",
                                                                                  "repo1",
                                                                                  "repo1-description"));
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                                  "namespace1",
                                                                                  "repo2",
                                                                                  "repo2-description"));
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                                  "global1",
                                                                                  "global1-description"));
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                                  "namespace2",
                                                                                  "repo3",
                                                                                  "repo3-description"));

        final List<ConfigGroup> repositoryNamespace1ConfigGroups = configurationService.getConfiguration(ConfigType.REPOSITORY,
                                                                                                         "namespace1");
        assertEquals(2,
                     repositoryNamespace1ConfigGroups.size());
        assertEquals("repo1",
                     repositoryNamespace1ConfigGroups.get(0).getName());
        assertEquals("repo2",
                     repositoryNamespace1ConfigGroups.get(1).getName());

        final List<ConfigGroup> repositoryNamespace2ConfigGroups = configurationService.getConfiguration(ConfigType.REPOSITORY,
                                                                                                         "namespace2");
        assertEquals(1,
                     repositoryNamespace2ConfigGroups.size());
        assertEquals("repo3",
                     repositoryNamespace2ConfigGroups.get(0).getName());

        final List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration(ConfigType.GLOBAL);
        assertEquals(1,
                     globalConfigGroups.size());
        assertEquals("global1",
                     globalConfigGroups.get(0).getName());
    }

    @Test(expected = RuntimeException.class)
    public void addAndGetConfigurationWithNamespaceButTypeDoesNotSupportNamespacesTest() {
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                                  "global1",
                                                                                  "global1-description"));

        configurationService.getConfiguration(ConfigType.GLOBAL,
                                              "namespace1");
    }

    @Test
    public void addAndGetConfigurationByNamespaceTest() {
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                                  "namespace1",
                                                                                  "repo1",
                                                                                  "repo1-description"));
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                                  "namespace1",
                                                                                  "repo2",
                                                                                  "repo2-description"));
        configurationService.addConfiguration(configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                                  "namespace2",
                                                                                  "repo3",
                                                                                  "repo3-description"));

        final Map<String, List<ConfigGroup>> configGroupsByNamespace = configurationService.getConfigurationByNamespace(ConfigType.REPOSITORY);
        assertEquals(2,
                     configGroupsByNamespace.size());

        final List<ConfigGroup> repositoryNamespace1ConfigGroups = configGroupsByNamespace.get("namespace1");
        assertEquals(2,
                     repositoryNamespace1ConfigGroups.size());
        assertEquals("repo1",
                     repositoryNamespace1ConfigGroups.get(0).getName());
        assertEquals("repo2",
                     repositoryNamespace1ConfigGroups.get(1).getName());

        final List<ConfigGroup> repositoryNamespace2ConfigGroups =  configGroupsByNamespace.get("namespace2");
        assertEquals(1,
                     repositoryNamespace2ConfigGroups.size());
        assertEquals("repo3",
                     repositoryNamespace2ConfigGroups.get(0).getName());
    }

    @Test
    public void updateConfigurationWithoutNamespaceTest() {
        final ConfigGroup config = configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                       "config",
                                                                       "description");
        configurationService.addConfiguration(config);
        config.setDescription("new-description");

        configurationService.updateConfiguration(config);

        final List<ConfigGroup> configGroups = configurationService.getConfiguration(ConfigType.GLOBAL);
        assertEquals(1,
                     configGroups.size());
        assertEquals("config",
                     configGroups.get(0).getName());
        assertEquals("new-description",
                     configGroups.get(0).getDescription());
    }

    @Test
    public void updateConfigurationWithNamespaceTest() {
        final ConfigGroup config = configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                       "namespace",
                                                                       "config",
                                                                       "description");
        configurationService.addConfiguration(config);
        config.setDescription("new-description");

        configurationService.updateConfiguration(config);

        final List<ConfigGroup> configGroups = configurationService.getConfiguration(ConfigType.REPOSITORY,
                                                                                     "namespace");
        assertEquals(1,
                     configGroups.size());
        assertEquals("config",
                     configGroups.get(0).getName());
        assertEquals("namespace",
                     configGroups.get(0).getNamespace());
        assertEquals("new-description",
                     configGroups.get(0).getDescription());
    }

    @Test
    public void removeConfigurationWithoutNamespaceTest() {
        final ConfigGroup config = configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                       "config",
                                                                       "description");

        configurationService.removeConfiguration(config);

        final List<ConfigGroup> configGroups = configurationService.getConfiguration(ConfigType.GLOBAL);
        assertEquals(0,
                     configGroups.size());
    }

    @Test
    public void removeConfigurationWithNamespaceTest() {
        final ConfigGroup config = configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                       "namespace",
                                                                       "config",
                                                                       "description");
        configurationService.addConfiguration(config);

        configurationService.removeConfiguration(config);

        final List<ConfigGroup> configGroups = configurationService.getConfiguration(ConfigType.REPOSITORY,
                                                                                     "namespace");
        assertEquals(0,
                     configGroups.size());
    }

    @Test
    public void cleanUpSystemRepositoryTest() {
        final ConfigGroup config = configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                       "namespace",
                                                                       "config",
                                                                       "description");
        configurationService.addConfiguration(config);

        final boolean result = configurationService.cleanUpSystemRepository();

        assertTrue(result);
    }

    private IOService mockIoService() {
        final IOService ioService = spy(fileSystemTestingUtils.getIoService());

        doNothing().when(ioService).startBatch(any(FileSystem.class));
        doNothing().when(ioService).endBatch();
        doReturn(fileSystemTestingUtils.getFileSystem()).when(ioService).newFileSystem(any(URI.class),
                                                                                       anyMap());

        return ioService;
    }
}
