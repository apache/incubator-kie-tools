/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.system.space.configuration;

import java.io.File;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.config.ConfigurationServiceImpl;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.server.config.ConfigType;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.migration.cli.MigrationSetup;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.project.cli.util.ConfigGroupToSpaceInfoConverter;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigGroupsMigrationServiceTest {

    private static final String SPACE_NAME = "MySpace";
    private static final String SPACE_GROUP = "com.myspace";
    private static final Boolean DELETED = Boolean.FALSE;

    private static final Integer CONTRIBUTORS = 1;
    private static final String CONTRIBUTOR = "admin";

    private static final Integer SPACE_REPOS = 2;

    private Boolean REPO_AVOID_INDEX = Boolean.FALSE;

    private static final String TEST_LEGACY_REPO = "testLegacy";
    private static final String TEST_MULTIPLE_INSTANCE_REPO = "test-multiple-instance";

    private static final File NIOGIT_DIR = Paths.get("target/.niogit").toFile();

    private SystemAccess system = new RealSystemAccess();

    private WeldContainer weldContainer;

    private ConfigurationServiceImpl configurationService;
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;
    private BackwardCompatibleUtil backwardCompatibleUtil;
    private ConfigGroupToSpaceInfoConverter configGroupToSpaceInfoConverter;

    private ConfigGroupsMigrationService migrationService;

    @Before
    public void init() {
        MigrationSetup.configureProperties(system, NIOGIT_DIR.toPath());

        weldContainer = new Weld().initialize();

        configurationService = spy(weldContainer.instance().select(ConfigurationServiceImpl.class).get());
        spaceConfigStorageRegistry = weldContainer.instance().select(SpaceConfigStorageRegistry.class).get();
        backwardCompatibleUtil = spy(weldContainer.instance().select(BackwardCompatibleUtil.class).get());
        configGroupToSpaceInfoConverter = spy(new ConfigGroupToSpaceInfoConverter(configurationService, backwardCompatibleUtil, spaceConfigStorageRegistry));

        migrationService = spy(new ConfigGroupsMigrationService(configurationService, spaceConfigStorageRegistry, configGroupToSpaceInfoConverter));
    }

    @Test
    public void testMigration() {

        migrationService.moveDataToSpaceConfigRepo();

        ArgumentCaptor<SpaceInfo> infoCaptor = ArgumentCaptor.forClass(SpaceInfo.class);

        verify(configurationService).getConfiguration(eq(ConfigType.SPACE));
        verify(configurationService, times(2)).getConfiguration(eq(ConfigType.REPOSITORY), eq(SPACE_NAME));
        verify(migrationService).saveSpaceInfo(infoCaptor.capture());
        verify(backwardCompatibleUtil, times(5)).compat(any());
        verify(configurationService).removeConfiguration(any());
        verify(configGroupToSpaceInfoConverter).cleanUpRepositories(any());

        SpaceInfo info = infoCaptor.getValue();

        Assertions.assertThat(info)
                .hasFieldOrPropertyWithValue("name", SPACE_NAME)
                .hasFieldOrPropertyWithValue("defaultGroupId", SPACE_GROUP);

        Assertions.assertThat(info.getContributors())
                .hasSize(CONTRIBUTORS);

        Assertions.assertThat(info.getContributors().iterator().next())
                .hasFieldOrPropertyWithValue("username", CONTRIBUTOR)
                .hasFieldOrPropertyWithValue("type", ContributorType.OWNER);

        info.getRepositories().forEach(repositoryInfo -> {
            Assertions.assertThat(repositoryInfo.getConfiguration().getEnvironment())
                    .doesNotContainKeys(EnvironmentParameters.USER_NAME,
                                        EnvironmentParameters.PASSWORD,
                                        EnvironmentParameters.SECURE_PREFIX + EnvironmentParameters.PASSWORD);
        });

        verify(configurationService).cleanUpSystemRepository();

        Assertions.assertThat(info.getSecurityGroups())
                .isEmpty();

        Assertions.assertThat(info.getRepositories())
                .hasSize(SPACE_REPOS);

        checkSpaceRepo(TEST_LEGACY_REPO, info);
        checkSpaceRepo(TEST_MULTIPLE_INSTANCE_REPO, info);
    }

    private void checkSpaceRepo(String repoName, SpaceInfo info) {
        RepositoryInfo spaceRepo = info.getRepositories().stream().filter(repo -> repo.getName().equals(repoName)).findAny().orElse(null);

        Assertions.assertThat(spaceRepo)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", repoName)
                .hasFieldOrPropertyWithValue("deleted", DELETED);

        assertEquals(SPACE_NAME, spaceRepo.getSpace());
        assertEquals(SpacesAPI.Scheme.GIT.toString(), spaceRepo.getScheme());
        assertEquals(REPO_AVOID_INDEX, spaceRepo.isAvoidIndex());

        Assertions.assertThat(spaceRepo.getContributors())
                .hasSize(CONTRIBUTORS);

        Assertions.assertThat(spaceRepo.getContributors().iterator().next())
                .hasFieldOrPropertyWithValue("username", CONTRIBUTOR)
                .hasFieldOrPropertyWithValue("type", ContributorType.OWNER);

        Assertions.assertThat(info.getSecurityGroups())
                .isEmpty();
    }
}

