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

package org.kie.workbench.common.system.configuration;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import javax.enterprise.event.Event;

import org.guvnor.structure.backend.config.ConfigGroupMarshaller;
import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.backend.config.ConfigurationServiceImpl;
import org.guvnor.structure.backend.config.DefaultPasswordServiceImpl;
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
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.kie.workbench.common.project.config.MigrationConfigurationFactoryImpl;
import org.kie.workbench.common.project.config.MigrationConfigurationServiceImpl;
import org.mockito.InjectMocks;
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
public class ConfigGroupsMigrationServiceTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Mock
    private org.guvnor.structure.repositories.Repository systemRepository;

    @Mock
    private Event<SystemRepositoryChangedEvent> repoChangedEvent;

    @Mock
    private Event<SystemRepositoryChangedEvent> spaceChangedEvent;

    @Mock
    private Event<SystemRepositoryChangedEvent> changedEvent;

    @Mock
    private User identity;

    @InjectMocks
    private RealSystemAccess system;

    private ConfigGroupMarshaller marshaller;

    private IOService ioService;

    private ConfigurationFactory oldConfigurationFactory;

    private ConfigurationService oldConfigurationService;

    private ConfigurationService newConfigurationService;

    private ConfigGroupsMigrationService configGroupsMigrationService;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();
        when(systemRepository.getUri()).thenReturn("git://amend-repo-test");

        marshaller = new ConfigGroupMarshaller();
        ioService = mockIoService();
        oldConfigurationFactory = new MigrationConfigurationFactoryImpl(new DefaultPasswordServiceImpl());
        oldConfigurationService = new MigrationConfigurationServiceImpl(systemRepository,
                                                                        marshaller,
                                                                        identity,
                                                                        ioService,
                                                                        repoChangedEvent,
                                                                        spaceChangedEvent,
                                                                        changedEvent,
                                                                        fileSystemTestingUtils.getFileSystem());
        newConfigurationService = new ConfigurationServiceImpl(systemRepository,
                                                               marshaller,
                                                               identity,
                                                               ioService,
                                                               repoChangedEvent,
                                                               spaceChangedEvent,
                                                               changedEvent,
                                                               fileSystemTestingUtils.getFileSystem());
        configGroupsMigrationService = new ConfigGroupsMigrationService(systemRepository,
                                                                        marshaller,
                                                                        ioService,
                                                                        fileSystemTestingUtils.getFileSystem(),
                                                                        system,
                                                                        identity);
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void groupSystemConfigGroupsTest() {
        createConfigGroupsWithOldConfigurationService();
        migrateConfigGroups();
        checkConfigGroupsWithNewConfigurationService();
    }

    private void createConfigGroupsWithOldConfigurationService() {
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                                        "global1",
                                                                                        "global1-description"));
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                                        "global2",
                                                                                        "global2-description"));
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.ORGANIZATIONAL_UNIT,
                                                                                        "ou1",
                                                                                        "ou1-description"));
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.ORGANIZATIONAL_UNIT,
                                                                                        "ou2",
                                                                                        "ou2-description"));
        final ConfigGroup repo1 = oldConfigurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                         "repo1",
                                                                         "repo1-description");
        repo1.addConfigItem(oldConfigurationFactory.newConfigItem("space",
                                                                  "ou1"));
        oldConfigurationService.addConfiguration(repo1);
        final ConfigGroup repo2 = oldConfigurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                         "repo2",
                                                                         "repo2-description");
        repo2.addConfigItem(oldConfigurationFactory.newConfigItem("space",
                                                                  "ou1"));
        oldConfigurationService.addConfiguration(repo2);
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.EDITOR,
                                                                                        "editor1",
                                                                                        "editor1-description"));
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.EDITOR,
                                                                                        "editor2",
                                                                                        "editor2-description"));
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.DEPLOYMENT,
                                                                                        "deployment1",
                                                                                        "deployment1-description"));
        oldConfigurationService.addConfiguration(oldConfigurationFactory.newConfigGroup(ConfigType.DEPLOYMENT,
                                                                                        "deployment2",
                                                                                        "deployment2-description"));
    }

    private void migrateConfigGroups() {
        configGroupsMigrationService.groupSystemConfigGroups();
    }

    private void checkConfigGroupsWithNewConfigurationService() {
        checkConfigGroupsOfAType(ConfigType.GLOBAL,
                                 2,
                                 "global1",
                                 "global2");
        checkConfigGroupsOfAType(ConfigType.ORGANIZATIONAL_UNIT,
                                 0);
        checkConfigGroupsOfAType(ConfigType.SPACE,
                                 2,
                                 "ou1",
                                 "ou2");
        checkConfigGroupsOfAType(ConfigType.REPOSITORY,
                                 "ou1",
                                 2,
                                 "repo1",
                                 "repo2");
        checkConfigGroupsOfAType(ConfigType.REPOSITORY,
                                 "ou2",
                                 0);
        checkConfigGroupsOfAType(ConfigType.EDITOR,
                                 2,
                                 "editor1",
                                 "editor2");
        checkConfigGroupsOfAType(ConfigType.DEPLOYMENT,
                                 2,
                                 "deployment1",
                                 "deployment2");
    }

    private void checkConfigGroupsOfAType(final ConfigType configType,
                                          final int size,
                                          final String... names) {
        final List<ConfigGroup> configGroups = newConfigurationService.getConfiguration(configType);
        checkConfigGroups(configGroups,
                          size,
                          names);
    }

    private void checkConfigGroupsOfAType(final ConfigType configType,
                                          final String namespace,
                                          final int size,
                                          final String... names) {
        final List<ConfigGroup> configGroups = newConfigurationService.getConfiguration(configType,
                                                                                        namespace);
        checkConfigGroups(configGroups,
                          size,
                          names);
    }

    private void checkConfigGroups(final List<ConfigGroup> configGroups,
                                   final int size,
                                   final String... names) {
        assertEquals(size,
                     configGroups.size());

        int i = 0;
        for (String name : names) {
            assertEquals(name,
                         configGroups.get(i++).getName());
        }
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
