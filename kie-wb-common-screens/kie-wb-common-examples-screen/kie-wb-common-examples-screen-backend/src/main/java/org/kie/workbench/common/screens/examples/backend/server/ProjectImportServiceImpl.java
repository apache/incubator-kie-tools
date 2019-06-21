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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.examples.model.Credentials;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ProjectImportService;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static java.util.stream.Collectors.toList;

@Service
@ApplicationScoped
public class ProjectImportServiceImpl extends BaseProjectImportService implements ProjectImportService {

    private static final Pattern STRIP_DOT_GIT = Pattern.compile("\\.git$");

    private Logger logger = LoggerFactory.getLogger(ProjectImportServiceImpl.class);
    private RepositoryFactory repositoryFactory;
    private final PathUtil pathUtil;
    private final Event<NewProjectEvent> newProjectEvent;
    private final RepositoryService repoService;

    private final Set<Repository> clonedRepositories = new HashSet<>();

    @Inject
    public ProjectImportServiceImpl(final @Named("ioStrategy") IOService ioService,
                                    final MetadataService metadataService,
                                    final RepositoryFactory repositoryFactory,
                                    final KieModuleService moduleService,
                                    final ImportProjectValidators validators,
                                    final PathUtil pathUtil,
                                    final WorkspaceProjectService projectService,
                                    final ProjectScreenService projectScreenService,
                                    final Event<NewProjectEvent> newProjectEvent,
                                    final RepositoryService repoService) {

        super(ioService,
              metadataService,
              validators,
              moduleService,
              projectService,
              projectScreenService);
        this.repositoryFactory = repositoryFactory;
        this.pathUtil = pathUtil;
        this.newProjectEvent = newProjectEvent;
        this.repoService = repoService;
    }

    @Override
    protected Repository resolveGitRepository(ExampleRepository repository) {

        try {
            String url = repository.getUrl();
            final String alias = getRepositoryAlias(url);
            Credentials credentials = repository.getCredentials();
            String username = null;
            String password = null;
            if (credentials != null) {
                username = credentials.getUsername();
                password = credentials.getPassword();
            }
            final Map<String, Object> env = this.buildGitEnv(url,
                                                             username,
                                                             password,
                                                             true);

            final RepositoryInfo repositoryConfig = createConfigGroup(alias,
                                                                      env);

            Repository repo = repositoryFactory.newRepository(repositoryConfig);
            clonedRepositories.add(repo);
            return repo;
        } catch (final Exception e) {
            logger.error("Error during create repository",
                         e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void dispose() {
        for (final Repository repository : clonedRepositories) {
            try {
                if (repository.getDefaultBranch().isPresent()) {
                    ioService.delete(Paths.convert(repository.getDefaultBranch().get().getPath()).getFileSystem().getPath(null));
                }
            } catch (Exception e) {
                logger.warn("Unable to remove transient Repository '" + repository.getAlias() + "'.",
                            e);
            }
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

        List<WorkspaceProject> importedProjects = projects.stream()
                .map(exampleProject -> {
                    WorkspaceProject project = this.importProject(activeOU,
                                                                  exampleProject);
                    return this.renameIfNecessary(activeOU,
                                                  project);
                })
                .collect(Collectors.toList());

        if (importedProjects.size() == 1) {
            final WorkspaceProject importedProject = importedProjects.get(0);
            return new WorkspaceProjectContextChangeEvent(importedProject,
                                                          importedProject.getMainModule());
        } else {
            return new WorkspaceProjectContextChangeEvent(activeOU);
        }
    }

    @Override
    public WorkspaceProject importProject(final OrganizationalUnit organizationalUnit,
                                          final ImportProject importProject) {
        final org.uberfire.java.nio.file.Path rootPath = getProjectRoot(importProject);
        String origin = importProject.getOrigin();
        if (pathUtil.convert(importProject.getRoot()).equals(rootPath)) {
            String username = null;
            String password = null;
            Credentials credentials = importProject.getCredentials();
            final List<String> branches = importProject.getSelectedBranches();
            if (credentials != null) {
                username = credentials.getUsername();
                password = credentials.getPassword();
            }
            return importProject(organizationalUnit,
                                 origin,
                                 username,
                                 password,
                                 branches);
        } else {
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
            final String subdirectoryPath = pathUtil.stripRepoNameAndSpace(pathUtil.stripProtocolAndBranch(importProject.getRoot().toURI()));
            configurations.setSubdirectory(subdirectoryPath);

            final Repository importedRepo = repoService.createRepository(organizationalUnit,
                                                                         GitRepository.SCHEME.toString(),
                                                                         importProject.getName(),
                                                                         configurations);

            // Signal creation of new Project (Creation of OU and Repository, if applicable,
            // are already handled in the corresponding services).
            final WorkspaceProject project = projectService.resolveProject(importedRepo);
            newProjectEvent.fire(new NewProjectEvent(project));

            return project;
        }
    }

    @Override
    public WorkspaceProject importProject(final OrganizationalUnit targetOU,
                                          final String repositoryURL,
                                          final String username,
                                          final String password,
                                          final List<String> branches) {
        final RepositoryEnvironmentConfigurations config = new RepositoryEnvironmentConfigurations();
        config.setOrigin(repositoryURL);
        if (username != null && password != null) {
            config.setUserName(username);
            config.setPassword(password);
        }
        config.setBranches(branches);

        final String targetProjectName = inferProjectName(repositoryURL);

        final Repository repo = repoService.createRepository(targetOU,
                                                             GitRepository.SCHEME.toString(),
                                                             targetProjectName,
                                                             config);
        return projectService.resolveProject(repo);
    }

    @Override
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
                                 getBranches(getProjectRoot(module.getRootPath()),
                                             module.getRootPath()),
                                 true);
    }

    private List<String> getBranches(final ImportProject importProject,
                                     final org.uberfire.java.nio.file.Path rootPath) {
        if (importProject.getSelectedBranches() == null || importProject.getSelectedBranches().isEmpty()) {
            return getBranches(rootPath,
                               importProject.getRoot());
        }

        return importProject.getSelectedBranches();
    }

    List<String> getBranches(final org.uberfire.java.nio.file.Path rootPath,
                             final Path projectPath) {
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

    String inferProjectName(String repositoryURL) {
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

    private org.uberfire.java.nio.file.Path getProjectRoot(final ImportProject importProject) {
        return getProjectRoot(importProject.getRoot());
    }

    private org.uberfire.java.nio.file.Path getProjectRoot(final Path rootPath) {
        return Stream.iterate(pathUtil.convert(rootPath),
                              p -> p.getParent())
                .filter(p -> p != null && p.getParent() == null)
                .findFirst()
                .get();
    }
}

