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

import java.nio.file.Path;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.project.WorkspaceProjectMigrationService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;

@Dependent
public class TemporaryNiogitService {

    static final String OU_OWNER = "admin";
    static final String OU_NAME = "migrationSpace";
    static final String TMP_REPO_ALIAS = "migrationRepo";

    private final RepositoryService repoService;
    private final WorkspaceProjectMigrationService migrationService;
    private final WorkspaceProjectService projectService;
    private final OrganizationalUnitService ouService;

    @Inject
    public TemporaryNiogitService(OrganizationalUnitService ouService,
                                  RepositoryService repoService,
                                  WorkspaceProjectService projectService,
                                  WorkspaceProjectMigrationService migrationService) {
        this.ouService = ouService;
        this.repoService = repoService;
        this.projectService = projectService;
        this.migrationService = migrationService;
    }

    public void importProjects(Path actualTarget) {
        OrganizationalUnit ou = ouService.createOrganizationalUnit(OU_NAME, OU_OWNER, "org.migration");
        String repositoryURL = actualTarget.toUri().toString();
        RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
        Map<String, Object> configMap = configurations.getConfigurationMap();
        configMap.put(EnvironmentParameters.AVOID_INDEX, "true");
        configMap.put("origin", repositoryURL);

        Repository repo = repoService.createRepository(ou, GitRepository.SCHEME.toString(), TMP_REPO_ALIAS, configurations);
        WorkspaceProject project = projectService.resolveProject(repo);
        migrationService.migrate(project);
        repoService.removeRepository(ou.getSpace(), TMP_REPO_ALIAS);
    }

}
