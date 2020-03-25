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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.project.WorkspaceProjectMigrationService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.project.cli.util.ConfigGroupToSpaceInfoConverter;
import org.kie.workbench.common.project.config.Migration;
import org.kie.workbench.common.project.config.MigrationRepositoryServiceImpl;

/**
 * <p>
 * Performs steps for project migration from within the workbench infrastructure.
 * Requires that repositories have already been migrated to spaces by the external service.
 */
@ApplicationScoped
public class InternalMigrationService {

    private WorkspaceProjectService projectService;
    private ConfigurationService configService;
    private WorkspaceProjectMigrationService projectMigrationService;
    private MigrationRepositoryServiceImpl repoService;
    private SystemAccess system;
    private ConfigGroupToSpaceInfoConverter configGroupToSpaceInfoConverter;
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Inject
    public InternalMigrationService(final @Migration WorkspaceProjectService projectService,
                                    final @Migration ConfigurationService configService,
                                    final @Migration WorkspaceProjectMigrationService projectMigrationService,
                                    final @Migration MigrationRepositoryServiceImpl repoService,
                                    final SystemAccess system,
                                    final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                                    final BackwardCompatibleUtil backwardCompatibleUtil) {
        this.projectService = projectService;
        this.configService = configService;
        this.projectMigrationService = projectMigrationService;
        this.repoService = repoService;
        this.system = system;
        this.configGroupToSpaceInfoConverter = new ConfigGroupToSpaceInfoConverter(configService, backwardCompatibleUtil,spaceConfigStorageRegistry);
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
    }

    public void migrateAllProjects(Path niogitDir) {
        List<ConfigGroup> orgUnitConfigs = configService.getConfiguration(ConfigType.ORGANIZATIONAL_UNIT);
        List<ConfigGroup> repoConfigs = configService.getConfiguration(ConfigType.REPOSITORY);
        Map<String, String> orgUnitByRepo = getOrgUnitsByRepo(orgUnitConfigs);

        if (repoConfigs.isEmpty()) {
            printNoReposMessage();
        } else {
            prepareRepositoriesForMigration(niogitDir,
                                            orgUnitConfigs,
                                            repoConfigs,
                                            orgUnitByRepo);
            migrateProjectsToIndividualRepos();

            configService.cleanUpSystemRepository();
        }
    }

    private void prepareRepositoriesForMigration(Path niogitDir,
                                                 List<ConfigGroup> orgUnitConfigs,
                                                 List<ConfigGroup> repoConfigs,
                                                 Map<String, String> orgUnitByRepo) {
        printFoundRepositoriesMessage(repoConfigs);
        addSpacesToRepoConfigs(orgUnitByRepo,
                               repoConfigs);
        migrateReposToSpaces(niogitDir,
                             orgUnitConfigs,
                             orgUnitByRepo);
    }

    private void printFoundRepositoriesMessage(List<ConfigGroup> repoConfigs) {
        system.out().printf("Found %s user %s:\n",
                            repoConfigs.size(),
                            repoConfigs.size() > 1 ? "repositories" : "repository");
        repoConfigs.forEach(group -> system.out().printf("\t%s\n",
                                                         group.getName()));
    }

    private void printNoReposMessage() {
        system.out().println("No user repositories found.");
    }

    private void migrateProjectsToIndividualRepos() {
        Collection<WorkspaceProject> allProjects = projectService.getAllWorkspaceProjects();
        printFoundProjectsMessage(allProjects);
        Set<Repository> cleanup = new LinkedHashSet<>();
        allProjects
                .forEach(proj -> {
                    system.out().printf("Migrating [%s]...\n",
                                        proj.getName());
                    cleanup.add(proj.getRepository());
                    projectMigrationService.migrate(proj);
                });
        cleanup.forEach(repo -> {
            system.out().printf("Removing migrated repository [%s]...\n",
                                repo.getAlias());
            this.repoService.deleteRepository(repo);
        });
        system.out().println("Finished project migration.");
    }

    private void printFoundProjectsMessage(Collection<WorkspaceProject> allProjects) {
        system.out().printf("Found %s %s:\n",
                            allProjects.size(),
                            allProjects.size() > 1 ? "projects" : "project");
        allProjects.forEach(proj -> system.out().printf("\t%s\n",
                                                        proj.getName()));
    }

    private void migrateReposToSpaces(Path niogitDir,
                                      List<ConfigGroup> orgUnitConfigs,
                                      Map<String, String> orgUnitByRepo) {
        createSpaceDirs(niogitDir,
                        orgUnitConfigs);
        moveRepos(niogitDir,
                  orgUnitByRepo);
    }

    private void moveRepos(Path niogitDir,
                           Map<String, String> orgUnitByRepo) {
        system.out().println("Moving repositories into spaces...");
        orgUnitByRepo
                .forEach((repo, ou) -> {
                    String repoFolderName = repo + ".git";
                    Path oldRepo = niogitDir.resolve(repoFolderName);
                    Path newRepo = niogitDir.resolve(ou).resolve(repoFolderName);
                    try {
                        system.out().printf("Moving repo [%s] into space [%s]...\n",
                                            repo,
                                            ou);
                        Files.move(oldRepo,
                                   newRepo);
                    } catch (IOException e) {
                        system.err().printf("Unable to move [%s].\n",
                                            oldRepo);
                        e.printStackTrace(system.err());
                    }
                });
        system.out().println("Finished moving repositories into space.");
    }

    public void createSpaceDirs(Path niogitDir,
                                List<ConfigGroup> orgUnitConfigs) {
        orgUnitConfigs
                .stream()
                .forEach(group -> {
                    Path ouSpace = niogitDir.resolve(group.getName());
                    ouSpace.toFile().mkdir();
                    SpaceInfo space = configGroupToSpaceInfoConverter.toSpaceInfo(group);
                    configGroupToSpaceInfoConverter.cleanUpRepositories(group);
                    spaceConfigStorageRegistry.get(group.getName()).saveSpaceInfo(space);
                });
    }

    private void addSpacesToRepoConfigs(Map<String, String> orgUnitByRepo,
                                        List<ConfigGroup> repoConfigs) {
        system.out().println("Updating repository configurations with spaces...");
        configService.startBatch();
        repoConfigs.forEach(group -> {
            String space = orgUnitByRepo.get(group.getName());
            if (space != null) {
                system.out().printf("Configuring repo [%s] in space [%s]...\n",
                                    group.getName(),
                                    space);
                ConfigItem<Object> item = new ConfigItem<>();
                item.setName(EnvironmentParameters.SPACE);
                item.setValue(space);
                group.setConfigItem(item);
                configService.updateConfiguration(group);
            }
        });
        configService.endBatch();
        system.out().println("Finished updating repository configurations.");
    }

    public Map<String, String> getOrgUnitsByRepo(List<ConfigGroup> orgUnitConfigs) {
        Map<String, String> orgUnitByRepo = new LinkedHashMap<>();
        orgUnitConfigs
                .stream()
                .forEach(group -> {
                    @SuppressWarnings("unchecked")
                    ConfigItem<List<String>> repos = group.getConfigItem("repositories");
                    Optional.ofNullable(repos)
                            .map(r -> r.getValue())
                            .ifPresent(r -> r.forEach(repo -> orgUnitByRepo.put(repo,
                                                                                group.getName())));
                });
        return orgUnitByRepo;
    }
}
