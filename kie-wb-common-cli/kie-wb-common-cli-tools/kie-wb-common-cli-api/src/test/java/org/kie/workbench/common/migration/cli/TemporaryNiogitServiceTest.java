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

package org.kie.workbench.common.migration.cli;

import java.io.File;
import java.nio.file.Path;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.project.WorkspaceProjectMigrationService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.Space;

import static org.guvnor.structure.repositories.EnvironmentParameters.AVOID_INDEX;
import static org.junit.Assert.*;
import static org.kie.workbench.common.migration.cli.TemporaryNiogitService.OU_NAME;
import static org.kie.workbench.common.migration.cli.TemporaryNiogitService.TMP_REPO_ALIAS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemporaryNiogitServiceTest {

    TemporaryNiogitService service;

    @Mock
    OrganizationalUnitService ouService;

    @Mock
    RepositoryService repoService;

    @Mock
    WorkspaceProjectService projectService;

    @Mock
    WorkspaceProjectMigrationService migrationService;

    @Captor
    ArgumentCaptor<WorkspaceProject> projectCaptor;

    @Captor
    ArgumentCaptor<RepositoryEnvironmentConfigurations> configsCaptor;

    Path target;

    @Before
    public void setup() {
        service = new TemporaryNiogitService(ouService,
                                             repoService,
                                             projectService,
                                             migrationService);
        target = new File("fake/path").toPath();

        when(ouService.createOrganizationalUnit(any(), any(), any(), any())).then(inv -> {
            String name = inv.getArgumentAt(0, String.class);
            String defaultGroupId = inv.getArgumentAt(1, String.class);
            return new OrganizationalUnitImpl(name, defaultGroupId);
        });

        when(repoService.createRepository(any(), any(), any(), any())).then(inv -> {
            String alias = inv.getArgumentAt(2, String.class);
            Space space = new Space(inv.getArgumentAt(0, OrganizationalUnit.class).getName());
            return new GitRepository(alias, space);
        });

        when(projectService.resolveProject(any(Repository.class))).then(inv -> {
            WorkspaceProject project = mock(WorkspaceProject.class);
            when(project.getRepository()).thenReturn(inv.getArgumentAt(0, Repository.class));

            return project;
        });
    }

    @Test
    public void removeMigrationRepoAfterMigration() throws Exception {
        service.importProjects(target);
        verify(repoService).removeRepository(eq(new Space(OU_NAME)), eq(TMP_REPO_ALIAS));
    }

    @Test
    public void migrateCalledOnLegacyProject() throws Exception {
        service.importProjects(target);

        verify(migrationService).migrate(projectCaptor.capture());
        WorkspaceProject migrated = projectCaptor.getValue();
        assertEquals(TMP_REPO_ALIAS, migrated.getRepository().getAlias());
    }

    @Test
    public void repoConfigurationHasOriginAndAvoidIndex() throws Exception {
        service.importProjects(target);

        verify(repoService).createRepository(any(), any(), any(), configsCaptor.capture());
        RepositoryEnvironmentConfigurations configs = configsCaptor.getValue();
        assertEquals(target.toUri().toString(), assertValue(configs.getOrigin(), String.class));
        assertEquals("true", configs.getConfigurationMap().get(AVOID_INDEX));
    }

    private <T> T assertValue(Object origin, Class<T> clazz) {
        assertTrue(clazz.isInstance(origin));
        return clazz.cast(origin);
    }

}
