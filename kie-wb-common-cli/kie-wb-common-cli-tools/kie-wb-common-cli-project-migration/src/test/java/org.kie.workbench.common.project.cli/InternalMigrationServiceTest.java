/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.project.cli;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.project.cli.util.ConfigGroupToSpaceInfoConverter;
import org.kie.workbench.common.project.config.MigrationConfigurationServiceImpl;
import org.kie.workbench.common.project.config.MigrationRepositoryServiceImpl;
import org.kie.workbench.common.project.config.MigrationWorkspaceProjectMigrationServiceImpl;
import org.kie.workbench.common.project.config.MigrationWorkspaceProjectServiceImpl;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InternalMigrationServiceTest {

    private static final String SPACE_CONTRIBUTORS = "space-contributors";
    private static final String SECURITY_GROUPS = "security:groups";

    @Mock
    private MigrationWorkspaceProjectServiceImpl projectService;

    @Mock
    private MigrationConfigurationServiceImpl configService;

    @Mock
    private MigrationWorkspaceProjectMigrationServiceImpl projectMigrationService;

    @Mock
    private MigrationRepositoryServiceImpl repoService;

    @Mock
    private SystemAccess system;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private BackwardCompatibleUtil backwardCompatibleUtil;

    private InternalMigrationService internalMigrationService;

    @Before
    public void init() {
        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(backwardCompatibleUtil.compat(any())).thenAnswer((Answer<ConfigGroup>) invocationOnMock -> (ConfigGroup) invocationOnMock.getArguments()[0]);

        internalMigrationService = new InternalMigrationService(projectService, configService, projectMigrationService, repoService, system, spaceConfigStorageRegistry, backwardCompatibleUtil);
    }

    @Test
    public void testGetOrgUnitsByRepo() {
        final String
                repo1Name = "repo1",
                repo2Name = "repo2",
                repo3Name = "repo3",
                space1Name = "space1",
                space2Name = "space2";

        final int numberOfRepositories = 3;

        List<ConfigGroup> ouConfigs = new ArrayList<>();

        List<String> space1Repos = Arrays.asList(new String[]{repo1Name, repo2Name});
        List<String> space2Repos = Arrays.asList(new String[]{repo3Name});

        ConfigItem<List<String>> space1Config = new ConfigItem<>();
        space1Config.setName("repositories");
        space1Config.setValue(space1Repos);

        ConfigItem<List<String>> space2Config = new ConfigItem<>();
        space2Config.setName("repositories");
        space2Config.setValue(space2Repos);

        ConfigGroup space1 = new ConfigGroup();
        space1.setConfigItem(space1Config);
        space1.setName(space1Name);

        ConfigGroup space2 = new ConfigGroup();
        space2.setConfigItem(space2Config);
        space2.setName(space2Name);

        ouConfigs.add(space1);
        ouConfigs.add(space2);

        Map<String, String> orgUnitsByRepo = internalMigrationService.getOrgUnitsByRepo(ouConfigs);

        assertEquals(numberOfRepositories, orgUnitsByRepo.size());
        assertEquals(space1Name, orgUnitsByRepo.get(repo1Name));
        assertEquals(space1Name, orgUnitsByRepo.get(repo2Name));
        assertEquals(space2Name, orgUnitsByRepo.get(repo3Name));
    }

    @Test
    public void testCreateSpaceDirs() {
        final String
                firstOuName = "firstOrgUnit",
                secondOuName = "secondOrgUnit";

        ConfigItem<List<Contributor>> contributors = new ConfigItem<>();
        contributors.setName(SPACE_CONTRIBUTORS);
        contributors.setValue(new ArrayList<>());

        ConfigItem<List<String>> groups = new ConfigItem<>();
        groups.setName(SECURITY_GROUPS);
        groups.setValue(new ArrayList<>());

        ConfigGroup firstOuConfig = new ConfigGroup();
        firstOuConfig.setName(firstOuName);
        firstOuConfig.addConfigItem(contributors);
        firstOuConfig.addConfigItem(groups);

        ConfigGroup secondOuConfig = new ConfigGroup();
        secondOuConfig.setName(secondOuName);
        secondOuConfig.addConfigItem(contributors);
        secondOuConfig.addConfigItem(groups);

        List<ConfigGroup> orgUnitConfigs = new ArrayList<>();
        orgUnitConfigs.add(firstOuConfig);
        orgUnitConfigs.add(secondOuConfig);

        Path mockPath = mock(Path.class);
        File mockFile = mock(File.class);

        when(mockPath.toFile()).thenReturn(mockFile);
        when(mockPath.resolve(anyString())).thenReturn(mockPath);

        internalMigrationService.createSpaceDirs(mockPath, orgUnitConfigs);

        verify(mockPath).resolve(firstOuName);
        verify(mockPath).resolve(secondOuName);
        verify(mockFile, times(2)).mkdir();
        verify(spaceConfigStorageRegistry, times(4)).get(anyString());
        verify(spaceConfigStorage, times(2)).saveSpaceInfo(any());
    }
}
