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
 *
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ImportService;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidator;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static java.util.stream.Collectors.toList;
import static org.guvnor.structure.repositories.EnvironmentParameters.MIRROR;
import static org.guvnor.structure.repositories.EnvironmentParameters.SCHEME;

public abstract class BaseProjectImportService implements ImportService {

    private static final String PROJECT_DESCRIPTON = "project.description";
    private static final String SYSTEM = "system";
    protected IOService ioService;
    protected MetadataService metadataService;
    protected ImportProjectValidators validators;
    protected KieModuleService moduleService;
    protected WorkspaceProjectService projectService;
    protected ProjectScreenService projectScreenService;
    protected SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    public BaseProjectImportService(final IOService ioService,
                                    final MetadataService metadataService,
                                    final ImportProjectValidators validators,
                                    final KieModuleService moduleService,
                                    final WorkspaceProjectService projectService,
                                    final ProjectScreenService projectScreenService,
                                    final SpaceConfigStorageRegistry spaceConfigStorageRegistry) {
        this.ioService = ioService;

        this.metadataService = metadataService;
        this.validators = validators;
        this.moduleService = moduleService;
        this.projectService = projectService;
        this.projectScreenService = projectScreenService;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
    }

    protected String getRepositoryAlias(String url) {
        String alias = url;
        alias = alias.substring(alias.lastIndexOf('/') + 1);
        final int lastDotIndex = alias.lastIndexOf('.');
        if (lastDotIndex > 0) {
            alias = alias.substring(0,
                                    lastDotIndex);
        }
        return alias;
    }

    protected List<String> getTags(final Module module) {
        List<String> tags = metadataService.getTags(module.getPomXMLPath());
        tags.sort(String::compareTo);
        return tags;
    }

    protected Set<ImportProject> convert(final Branch branch,
                                         final ExampleRepository repository) {
        final Set<Module> modules = moduleService.getAllModules(branch);
        return modules.stream()
                .map(p -> makeExampleProject(p,
                                             repository))
                .collect(Collectors.toSet());
    }

    protected ImportProject makeExampleProject(final Module module,
                                               ExampleRepository repository) {
        final String description = readDescription(module);
        final List<String> tags = getTags(module);

        return new ImportProject(module.getRootPath(),
                                 module.getModuleName(),
                                 description,
                                 repository.getUrl(),
                                 tags,
                                 repository.getCredentials(),
                                 Collections.emptyList(),
                                 false);
    }

    protected String readDescription(final Module module) {
        final Path root = module.getRootPath();
        final POM pom = module.getPom();
        final org.uberfire.java.nio.file.Path nioRoot = Paths.convert(root);
        final org.uberfire.java.nio.file.Path nioDescription = nioRoot.resolve(PROJECT_DESCRIPTON);
        String description = "Example '" + module.getModuleName() + "' module";

        if (ioService.exists(nioDescription)) {
            description = ioService.readAllString(nioDescription);
        } else if (pom != null
                && pom.getDescription() != null
                && !pom.getDescription().isEmpty()) {
            description = pom.getDescription();
        }

        if (description != null) {
            return description.replaceAll("\\s+",
                                          " ");
        }

        return description;
    }

    protected Set<ImportProject> validateProjects(Set<ImportProject> projects) {
        return projects
                .stream()
                .map(project -> {
                    List<ExampleProjectError> errors = getValidators().stream()
                            .map(exampleProjectValidation -> exampleProjectValidation.validate(project))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(toList());
                    return new ImportProject(project.getRoot(),
                                             project.getName(),
                                             project.getDescription(),
                                             project.getOrigin(),
                                             project.getTags(),
                                             errors,
                                             project.getCredentials(),
                                             project.getAllBranches(),
                                             project.getSelectedBranches(),
                                             project.canSelectBranches());
                })
                .collect(Collectors.toSet());
    }

    protected RepositoryInfo createConfigGroup(String alias,
                                               Map<String, Object> env) {

        RepositoryConfiguration configuration = new RepositoryConfiguration(env);

        configuration.add(EnvironmentParameters.AVOID_INDEX,
                          true);

        configuration.add(EnvironmentParameters.SPACE,
                          this.getDefaultSpace());

        RepositoryInfo repositoryConfig = new RepositoryInfo(alias,
                                                             false,
                                                             configuration);

        return repositoryConfig;
    }

    protected String getDefaultSpace() {
        return SYSTEM;
    }

    protected Map<String, Object> buildGitEnv(String url,
                                              String username,
                                              String password,
                                              boolean mirror) {
        return new HashMap<String, Object>() {{
            put("origin",
                url);
            put(SCHEME,
                GitRepository.SCHEME.toString());
            put("replaceIfExists",
                false);
            put("username",
                username);
            put("password",
                password);
            put(MIRROR,
                mirror);
        }};
    }

    protected WorkspaceProject renameIfNecessary(final OrganizationalUnit ou,
                                                 final WorkspaceProject project) {

        String name = project.getName();
        Collection<WorkspaceProject> projectsWithSameName = projectService.getAllWorkspaceProjectsByName(ou,
                                                                                                         name);

        if (projectsWithSameName.size() > 1) {
            name = this.projectService.createFreshProjectName(ou,
                                                              project.getName());
        }

        if (!name.equals(project.getName())) {
            final Path pomXMLPath = project.getMainModule().getPomXMLPath();
            final ProjectScreenModel model = projectScreenService.load(pomXMLPath);
            model.getPOM().setName(name);
            projectScreenService.save(pomXMLPath,
                                      model,
                                      "");
            return projectService.resolveProject(pomXMLPath);
        }

        return project;
    }

    @Override
    public Set<ImportProject> getProjects(final ExampleRepository repository) {

        if (repository == null) {
            return Collections.emptySet();
        }
        final String repositoryURL = repository.getUrl();
        if (repositoryURL == null || repositoryURL.trim().isEmpty()) {
            return Collections.emptySet();
        }

        Repository gitRepository = resolveGitRepository(repository);

        if (gitRepository == null) {
            return Collections.emptySet();
        }

        Set<ImportProject> importProjects = convert(gitRepository.getBranch("master").get(),
                                                    repository);
        return validateProjects(importProjects);
    }

    protected List<ImportProjectValidator> getValidators() {
        return this.validators.getValidators();
    }

    protected abstract Repository resolveGitRepository(final ExampleRepository exampleRepository);
}
