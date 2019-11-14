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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
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
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.examples.exception.ProjectAlreadyExistException;
import org.kie.workbench.common.screens.examples.model.Credentials;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ImportService;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidator;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

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
    private PathUtil pathUtil;
    private RepositoryService repoService;
    private Event<NewProjectEvent> newProjectEvent;

    public BaseProjectImportService(final IOService ioService,
                                    final MetadataService metadataService,
                                    final ImportProjectValidators validators,
                                    final KieModuleService moduleService,
                                    final WorkspaceProjectService projectService,
                                    final ProjectScreenService projectScreenService,
                                    final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                                    final PathUtil pathUtil,
                                    final RepositoryService repoService,
                                    final Event<NewProjectEvent> newProjectEvent) {
        this.ioService = ioService;

        this.metadataService = metadataService;
        this.validators = validators;
        this.moduleService = moduleService;
        this.projectService = projectService;
        this.projectScreenService = projectScreenService;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
        this.pathUtil = pathUtil;
        this.repoService = repoService;
        this.newProjectEvent = newProjectEvent;
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

    protected Set<ImportProject> validateProjects(OrganizationalUnit targetOu, Set<ImportProject> projects) {
        return projects
                .stream()
                .map(project -> {
                    List<ExampleProjectError> errors = getValidators().stream()
                            .map(exampleProjectValidation -> exampleProjectValidation.validate(targetOu, project))
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

    @Override
    public boolean exist(final OrganizationalUnit ou,
                         final ImportProject project) {
        String name = project.getName();
        Collection<WorkspaceProject> projectsWithSameName = projectService.getAllWorkspaceProjectsByName(ou,
                                                                                                         name);
        return !projectsWithSameName.isEmpty();
    }

    public void checkIfProjectAlreadyExist(final OrganizationalUnit ou,
                                           final ImportProject project) {

        if (this.exist(ou, project)) {
            String message = "Space [{0}] already contains a project with name [{1}]. " +
                    "Please delete existing project or create a new one with a different name";
            throw new ProjectAlreadyExistException(MessageFormat.format(message,
                                                                        ou.getSpace().getName(),
                                                                        project.getName()));
        }
    }

    @Override
    public Set<ImportProject> getProjects(final OrganizationalUnit target, final ExampleRepository repository) {

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
        return validateProjects(target, importProjects);
    }

    protected List<ImportProjectValidator> getValidators() {
        return this.validators.getValidators();
    }

    @Override
    public WorkspaceProject importProject(OrganizationalUnit organizationalUnit, ImportProject importProject) {
        this.checkIfProjectAlreadyExist(organizationalUnit,
                                        importProject);
        final org.uberfire.java.nio.file.Path rootPath = getProjectRoot(importProject);
        String origin = importProject.getOrigin();
        final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
        configurations.setInit(false);
        configurations.setOrigin(origin);
        configurations.setBranches(getBranches(importProject,
                                               rootPath));
        Credentials credentials = importProject.getCredentials();
        if (credentials != null && credentials.getUsername() != null && credentials.getPassword() != null) {
            configurations.setUserName(credentials.getUsername());
            configurations.setPassword(credentials.getPassword());
        }
        configurations.setMirror(false);

        String projectName = importProject.getName();
        if (!pathUtil.convert(importProject.getRoot()).equals(rootPath)) {
            final String subdirectoryPath = pathUtil.stripRepoNameAndSpace(pathUtil.stripProtocolAndBranch(importProject.getRoot().toURI()));
            configurations.setSubdirectory(subdirectoryPath);
        } else {
            projectName = inferProjectName(importProject.getOrigin());
        }

        final Repository importedRepo = repoService.createRepository(organizationalUnit,
                                                                     GitRepository.SCHEME.toString(),
                                                                     projectName,
                                                                     configurations);

        // Signal creation of new Project (Creation of OU and Repository, if applicable,
        // are already handled in the corresponding services).
        WorkspaceProject project = projectService.resolveProject(importedRepo);
        return project;
    }

    protected List<String> getBranches(final org.uberfire.java.nio.file.Path rootPath,
                                       final org.uberfire.backend.vfs.Path projectPath) {
        final FileSystem fs = rootPath.getFileSystem();
        final String exampleRootPath = pathUtil.stripRepoNameAndSpace(pathUtil.stripProtocolAndBranch(projectPath.toURI()));
        return StreamSupport.stream(fs.getRootDirectories().spliterator(),
                                    false)
                .filter(root -> exists(root.resolve(exampleRootPath)))
                .map(pathUtil::convert)
                .map(root -> pathUtil.extractBranch(root.toURI()))
                .flatMap(oBranch -> oBranch.map(Stream::of).orElse(Stream.empty()))
                .collect(toList());
    }

    protected List<String> getBranches(final ImportProject importProject,
                                       final org.uberfire.java.nio.file.Path rootPath) {
        if (importProject.getSelectedBranches() == null || importProject.getSelectedBranches().isEmpty()) {
            return getBranches(rootPath,
                               importProject.getRoot());
        }

        return importProject.getSelectedBranches();
    }

    protected org.uberfire.java.nio.file.Path getProjectRoot(final ImportProject importProject) {
        return getProjectRoot(importProject.getRoot());
    }

    protected org.uberfire.java.nio.file.Path getProjectRoot(final org.uberfire.backend.vfs.Path rootPath) {
        return Stream.iterate(pathUtil.convert(rootPath),
                              p -> p.getParent())
                .filter(p -> p != null && p.getParent() == null)
                .findFirst()
                .get();
    }

    private boolean exists(org.uberfire.java.nio.file.Path path) {
        try {
            final FileSystemProvider provider = path.getFileSystem().provider();
            provider.readAttributes(path,
                                    BasicFileAttributes.class);

            return true;
        } catch (NoSuchFileException nfe) {
            return false;
        }
    }

    @Override
    public WorkspaceProjectContextChangeEvent importProjects(OrganizationalUnit activeOU,
                                                             List<ImportProject> projects) {

        PortablePreconditions.checkNotNull("activeOU",
                                           activeOU);
        PortablePreconditions.checkNotNull("projects",
                                           projects);
        PortablePreconditions.checkCondition("Must have at least one Project",
                                             projects.size() > 0);

        return spaceConfigStorageRegistry.getBatch(activeOU.getSpace().getName())
                .run(context -> {
                    List<WorkspaceProject> importedProjects = projects.stream()
                            .map(exampleProject -> {
                                WorkspaceProject project = this.importProject(activeOU,
                                                                              exampleProject);

                                newProjectEvent.fire(new NewProjectEvent(project));

                                return project;
                            })
                            .collect(Collectors.toList());

                    if (importedProjects.size() == 1) {
                        final WorkspaceProject importedProject = importedProjects.get(0);
                        return new WorkspaceProjectContextChangeEvent(importedProject,
                                                                      importedProject.getMainModule());
                    } else {
                        return new WorkspaceProjectContextChangeEvent(activeOU);
                    }
                });
    }

    protected String inferProjectName(String repositoryURL) {
        repositoryURL = repositoryURL.replaceAll("\\\\",
                                                 "/");
        if (repositoryURL.endsWith(".git")) {
            repositoryURL = repositoryURL.substring(0,
                                                    repositoryURL.length() - 4);
        }
        if (repositoryURL.endsWith("/")) {
            repositoryURL = repositoryURL.substring(0,
                                                    repositoryURL.length() - 1);
        }
        if (repositoryURL.lastIndexOf('/') < 0) {
            return "new-project";
        }
        return repositoryURL.substring(repositoryURL.lastIndexOf('/') + 1);
    }

    protected abstract Repository resolveGitRepository(final ExampleRepository exampleRepository);
}
