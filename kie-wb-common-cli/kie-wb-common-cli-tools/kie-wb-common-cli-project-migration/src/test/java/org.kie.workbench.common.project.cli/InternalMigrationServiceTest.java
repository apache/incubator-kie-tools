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

import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InternalMigrationServiceTest {

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

        Map<String, String> orgUnitsByRepo = InternalMigrationService.getOrgUnitsByRepo(ouConfigs);

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

        final int expectedCreatedDirs = 2;

        ConfigGroup firstOuConfig = new ConfigGroup();
        firstOuConfig.setName(firstOuName);

        ConfigGroup secondOuConfig = new ConfigGroup();
        secondOuConfig.setName(secondOuName);

        List<ConfigGroup> orgUnitConfigs = new ArrayList<>();
        orgUnitConfigs.add(firstOuConfig);
        orgUnitConfigs.add(secondOuConfig);

        Path mockPath = mock(Path.class);
        File mockFile = mock(File.class);

        when(mockPath.toFile()).thenReturn(mockFile);
        when(mockPath.resolve(anyString())).thenReturn(mockPath);

        InternalMigrationService.createSpaceDirs(mockPath, orgUnitConfigs);

        verify(mockPath).resolve(firstOuName);
        verify(mockPath).resolve(secondOuName);
        verify(mockFile, times(expectedCreatedDirs)).mkdir();
    }
}
