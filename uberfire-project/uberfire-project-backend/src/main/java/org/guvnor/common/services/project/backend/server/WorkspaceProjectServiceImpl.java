/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.project.utils.NewWorkspaceProjectUtils;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.GitHookSupport;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class WorkspaceProjectServiceImpl
        implements WorkspaceProjectService {

    private static final String GIT_SCHEME = "git";
    private static final String REMOTE_ORIGIN_REF = "refs/remotes/origin/master";
    private static final String ORIGIN_KEY = "origin";

    private OrganizationalUnitService organizationalUnitService;
    private RepositoryService repositoryService;
    private Event<NewProjectEvent> newProjectEvent;
    private Event<RepositoryUpdatedEvent> repositoryUpdatedEvent;
    private Event<NewBranchEvent> newBranchEvent;
    private ModuleService<? extends Module> moduleService;
    private SpacesAPI spaces;
    private ModuleRepositoryResolver repositoryResolver;
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;
    private Logger logger = LoggerFactory.getLogger(WorkspaceProjectServiceImpl.class);
    private IOService ioService;
    private PathUtil pathUtil;
    private ChangeRequestService changeRequestService;
    private POMService pomService;

    public WorkspaceProjectServiceImpl() {
    }

    @Inject
    public WorkspaceProjectServiceImpl(final OrganizationalUnitService organizationalUnitService,
                                       final RepositoryService repositoryService,
                                       final SpacesAPI spaces,
                                       final Event<NewProjectEvent> newProjectEvent,
                                       final Event<RepositoryUpdatedEvent> repositoryUpdatedEvent,
                                       final Event<NewBranchEvent> newBranchEvent,
                                       final Instance<ModuleService<? extends Module>> moduleServices,
                                       final ModuleRepositoryResolver repositoryResolver,
                                       @Named("ioStrategy") final IOService ioService,
                                       final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                                       final PathUtil pathUtil,
                                       final ChangeRequestService changeRequestService,
                                       final POMService pomService) {
        this.organizationalUnitService = organizationalUnitService;
        this.repositoryService = repositoryService;
        this.spaces = spaces;
        this.newProjectEvent = newProjectEvent;
        this.repositoryUpdatedEvent = repositoryUpdatedEvent;
        this.newBranchEvent = newBranchEvent;
        this.moduleService = moduleServices.get();
        this.repositoryResolver = repositoryResolver;
        this.ioService = ioService;
        this.pathUtil = pathUtil;
        this.changeRequestService = changeRequestService;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
        this.pomService = pomService;
    }

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjects() {

        final List<WorkspaceProject> result = new ArrayList<>();

        for (final OrganizationalUnit ou : organizationalUnitService.getOrganizationalUnits()) {
            result.addAll(getAllWorkspaceProjects(ou));
        }

        return result;
    }

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjects(final OrganizationalUnit organizationalUnit) {
        return getAllWorkspaceProjectsByName(organizationalUnit,
                                             null);
    }

    @Override
    public Collection<WorkspaceProject> getAllWorkspaceProjectsByName(final OrganizationalUnit organizationalUnit,
                                                                      final String name) {
        return this.getAllWorkspaceProjectsByName(organizationalUnit,
                                                  name,
                                                  false);
    }

    public Collection<WorkspaceProject> getAllWorkspaceProjectsByName(final OrganizationalUnit organizationalUnit,
                                                                      final String name,
                                                                      final boolean includeDeleted) {
        final List<WorkspaceProject> result = new ArrayList<>();

        Space space = spaces.getSpace(organizationalUnit.getName());

        for (final Repository repository : repositoryService.getAllRepositories(space,
                                                                                includeDeleted)) {

            if (repository.getDefaultBranch().isPresent()) {

                final WorkspaceProject project = new WorkspaceProject(organizationalUnit,
                                                                      repository,
                                                                      repository.getDefaultBranch().get(),
                                                                      moduleService.resolveModule(repository.getDefaultBranch().get().getPath()));
                if (name == null || name.equals(project.getName())) {
                    result.add(project);
                }
            }
        }
        return result;
    }

    @Override
    public boolean spaceHasNoProjectsWithName(final OrganizationalUnit organizationalUnit,
                                              final String name,
                                              final WorkspaceProject projectToIgnore) {
        return getAllWorkspaceProjectsByName(organizationalUnit,
                                             name)
                .stream().noneMatch(p -> !p.getEncodedIdentifier().equals(projectToIgnore.getEncodedIdentifier()));
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom) {
        return newProject(organizationalUnit,
                          pom,
                          DeploymentMode.VALIDATED);
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode) {
        return newProject(organizationalUnit,
                          pom,
                          mode,
                          null);
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode,
                                       final List<Contributor> contributors) {
        return newProject(organizationalUnit,
                          pom,
                          mode,
                          contributors,
                          null);
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode,
                                       final List<Contributor> contributors,
                                       final Repository templateRepository) {
        return newProject(organizationalUnit,
                          pom,
                          mode,
                          contributors,
                          templateRepository,
                          null);
    }

    @Override
    public WorkspaceProject newProject(final OrganizationalUnit organizationalUnit,
                                       final POM pom,
                                       final DeploymentMode mode,
                                       final List<Contributor> contributors,
                                       final Repository templateRepository,
                                       final String remoteRepositoryUrl) {

        return spaceConfigStorageRegistry.getBatch(organizationalUnit.getSpace().getName())
                .run(context -> {
                    final String newName = this.createFreshProjectName(organizationalUnit, pom.getName());

                    pom.setName(newName);

                    if (DeploymentMode.VALIDATED.equals(mode)) {
                        checkRepositories(pom);
                    }

                    String repositoryAlias = this.createFreshRepositoryAlias(organizationalUnit, pom.getName());

                    final boolean createFromTemplate = templateRepository != null;
                    final RepositoryEnvironmentConfigurations configurations = createFromTemplate
                            ? createRepositoryConfigForTemplate(templateRepository)
                            : new RepositoryEnvironmentConfigurations();

                    final Repository repository = repositoryService.createRepository(organizationalUnit,
                                                                                     GIT_SCHEME,
                                                                                     repositoryAlias,
                                                                                     configurations,
                                                                                     contributors != null ? contributors : Collections.emptyList());

                    try {
                        final Branch defaultBranch = resolveDefaultBranch(repository);

                        final Module module = createFromTemplate
                                ? finishCreateFromTemplate(repository, templateRepository, pom)
                                : moduleService.newModule(defaultBranch.getPath(), pom, mode);

                        if (remoteRepositoryUrl != null) {
                            addRemoteOrigin(repository,
                                            remoteRepositoryUrl);

                            executePostCommitHook(repository);
                        }

                        final WorkspaceProject workspaceProject = new WorkspaceProject(organizationalUnit,
                                                                                       repository,
                                                                                       defaultBranch,
                                                                                       module);

                        newProjectEvent.fire(new NewProjectEvent(workspaceProject));

                        return workspaceProject;
                    } catch (Exception e) {
                        logger.error("Error trying to create project", e);
                        logger.error("Error trying to create project " + organizationalUnit.getName() + " - " + repository.getAlias(), e);
                        try {
                            this.repositoryService.removeRepository(this.spaces.getSpace(organizationalUnit.getName()), repository.getAlias());
                        } catch (Exception ex) {
                            logger.error("Error trying to delete repository", ex);
                            logger.error("Error trying to delete repository " + organizationalUnit.getName() + " - " + repository.getAlias(), ex);
                            throw ExceptionUtilities.handleException(ex);
                        }
                        throw ExceptionUtilities.handleException(e);
                    }
                });
    }

    private RepositoryEnvironmentConfigurations createRepositoryConfigForTemplate(final Repository templateRepository) {
        final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();

        final File repositoryDirectory = resolveRepositoryDirectory(templateRepository);

        configurations.setInit(false);
        configurations.setOrigin(repositoryDirectory.toURI().toString());
        configurations.setMirror(false);

        return configurations;
    }

    private File resolveRepositoryDirectory(final Repository repository) {
        final org.uberfire.java.nio.fs.jgit.util.Git git = resolveGit(repository);

        return git.getRepository().getDirectory();
    }

    private Branch resolveDefaultBranch(final Repository repository) {
        return repository.getDefaultBranch()
                .orElseThrow(() -> new IllegalStateException("New repository should always have a branch."));
    }

    private org.uberfire.java.nio.fs.jgit.util.Git resolveGit(final Repository repository) {
        return resolveJGitFileSystem(repository).getGit();
    }

    private void executePostCommitHook(final Repository repository) {
        final JGitFileSystem fs = resolveJGitFileSystem(repository);
        ((GitHookSupport) fs.provider()).executePostCommitHook(fs);
    }

    private JGitFileSystem resolveJGitFileSystem(final Repository repository) {
        final Branch defaultBranch = resolveDefaultBranch(repository);

        return ((JGitPathImpl) pathUtil.convert(defaultBranch.getPath())).getFileSystem();
    }

    private Module finishCreateFromTemplate(final Repository projectRepository,
                                            final Repository templateRepository,
                                            final POM pom) {
        cleanUpTemplateOrigin(projectRepository);

        updateTemplatePOM(projectRepository,
                          templateRepository,
                          pom);

        final Branch defaultBranch = resolveDefaultBranch(projectRepository);

        moduleService.createModuleDirectories(defaultBranch.getPath());

        return moduleService.resolveModule(Paths.convert(Paths.convert(defaultBranch.getPath()).getRoot()));
    }

    private void cleanUpTemplateOrigin(final Repository repository) {
        final org.uberfire.java.nio.fs.jgit.util.Git git = resolveGit(repository);

        git.removeRemote(ORIGIN_KEY,
                         REMOTE_ORIGIN_REF);
    }

    private void addRemoteOrigin(final Repository repository,
                                 final String remoteRepositoryUrl) {
        final org.uberfire.java.nio.fs.jgit.util.Git git = resolveGit(repository);

        git.addRemote(ORIGIN_KEY,
                      remoteRepositoryUrl);
    }

    private void updateTemplatePOM(final Repository projectRepository,
                                   final Repository templateRepository,
                                   final POM pom) {
        final Path repositoryRoot = resolveDefaultBranch(projectRepository).getPath();
        final Path templatePath = resolveDefaultBranch(templateRepository).getPath();
        final Path pomPath = resolvePathFromParent(repositoryRoot, POMServiceImpl.POM_XML);
        final Path templatePomPath = resolvePathFromParent(templatePath, POMServiceImpl.POM_XML);
        final POM templatePom = pomService.load(templatePomPath);

        templatePom.setName(pom.getName());
        templatePom.setDescription(pom.getDescription());

        templatePom.getGav().setGroupId(pom.getGav().getGroupId());
        templatePom.getGav().setArtifactId(pom.getGav().getArtifactId());
        templatePom.getGav().setVersion(pom.getGav().getVersion());

        pomService.save(pomPath,
                        templatePom,
                        null,
                        "Updating the POM file");
    }

    Path resolvePathFromParent(final Path parent,
                               final String toResolve) {
        return Paths.convert(Paths.convert(parent).resolve(toResolve));
    }

    String createFreshRepositoryAlias(final OrganizationalUnit organizationalUnit,
                                      final String projectName) {
        int index = 0;
        String suffix = "";
        String repositoryAlias = checkNotNull("project name in pom model", NewWorkspaceProjectUtils.sanitizeProjectName(projectName));

        while (repositoryService.getRepositoryFromSpace(organizationalUnit.getSpace(), repositoryAlias + suffix) != null) {
            suffix = "-" + ++index;
        }

        return repositoryAlias + suffix;
    }

    @Override
    public String createFreshProjectName(final OrganizationalUnit organizationalUnit,
                                         final String name) {
        int index = 0;
        String suffix = "";
        while (!this.getAllWorkspaceProjectsByName(organizationalUnit,
                                                   name + suffix,
                                                   true).isEmpty()) {
            suffix = "-" + ++index;
        }

        return name + suffix;
    }

    @Override
    public WorkspaceProject resolveProject(final Repository repository) {

        if (!repository.getDefaultBranch().isPresent()) {
            throw new IllegalStateException("New repository should always have a branch.");
        }

        return resolveProject(repository.getSpace(),
                              repository.getDefaultBranch().get());
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Branch branch) {
        return resolveProject(space,
                              branch.getPath());
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Module module) {
        return resolveProject(space,
                              module.getRootPath());
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final String name) {

        OrganizationalUnit ou = organizationalUnitService.getOrganizationalUnit(space.getName());
        return resolveProject(ou,
                              name);
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final String projectName,
                                           final String branchName) {

        WorkspaceProject workspaceProject = resolveProject(space, projectName);

        if (workspaceProject == null) {
            throw new IllegalArgumentException("project " + projectName + " not found.");
        }

        if (branchName == null) {
            return workspaceProject;
        }

        for (final Branch branch : workspaceProject.getRepository().getBranches()) {
            if (branch.getName().equals(branchName)) {
                return resolveProject(space, branch);
            }
        }

        throw new IllegalArgumentException("branch " + branchName + " not found.");
    }

    private WorkspaceProject resolveProject(OrganizationalUnit ou,
                                            final String name) {
        for (final WorkspaceProject workspaceProject : getAllWorkspaceProjects(ou)) {
            if (workspaceProject.getName().equals(name)) {
                return workspaceProject;
            }
        }

        return null;
    }

    @Override
    public WorkspaceProject resolveProjectByRepositoryAlias(final Space space,
                                                            final String repositoryAlias) {
        return resolveProject(repositoryService.getRepositoryFromSpace(space,
                                                                       repositoryAlias));
    }

    @Override
    public WorkspaceProject resolveProject(final Space space,
                                           final Path path) {

        final org.uberfire.java.nio.file.Path repositoryRoot = Paths.convert(path).getRoot();

        final Repository repository = repositoryService.getRepository(space,
                                                                      Paths.convert(repositoryRoot));

        if (repository == null) {
            throw new RuntimeException("Repository not found inside space " + space.getName() + " with path " + path.toURI() + " (root path " + repositoryRoot.toUri() + ")");
        }

        final Branch branch = resolveBranch(repositoryRoot,
                                            repository);

        return new WorkspaceProject(organizationalUnitService.getOrganizationalUnit(repository.getSpace().getName()),
                                    repository,
                                    branch,
                                    moduleService.resolveModule(Paths.convert(Paths.convert(branch.getPath()).getRoot())));
    }

    @Override
    public WorkspaceProject resolveProject(Path path) {
        return spaces
                .resolveSpace(path.toURI())
                .map(space -> resolveProject(space,
                                             path))
                .orElseThrow(() -> new IllegalArgumentException("Could not determine space containing path: " + path));
    }

    private Branch resolveBranch(final org.uberfire.java.nio.file.Path repositoryRoot,
                                 final Repository repository) {
        if (!repository.getDefaultBranch().isPresent()) {
            throw new RuntimeException("Default branch not found in repository " + repository.getAlias() + " with path " + repositoryRoot.toUri());
        }

        final Branch defaultBranch = repository.getDefaultBranch().get();

        if (!Paths.convert(defaultBranch.getPath()).equals(repositoryRoot)) {

            for (final Branch branch : repository.getBranches()) {

                if (Paths.convert(branch.getPath()).equals(repositoryRoot)) {
                    return branch;
                }
            }
        }
        return defaultBranch;
    }

    private void checkRepositories(final POM pom) {
        final Set<MavenRepositoryMetadata> repositories = repositoryResolver.getRepositoriesResolvingArtifact(pom.getGav());
        if (repositories.size() > 0) {
            throw new GAVAlreadyExistsException(pom.getGav(),
                                                repositories);
        }
    }

    @Override
    public void addBranch(final String newBranchName,
                          final String baseBranchName,
                          final WorkspaceProject project,
                          final String userIdentifier) {

        final Branch baseBranch = project
            .getRepository()
            .getBranch(baseBranchName)
            .orElseThrow(() -> new IllegalStateException("The base branch [" + baseBranchName + "] does not exists"));

        final org.uberfire.java.nio.file.Path baseBranchPath = pathUtil.convert(baseBranch.getPath());
        final String newBranchPathURI = pathUtil.replaceBranch(newBranchName,
                                                               baseBranch.getPath().toURI());
        try {
            final org.uberfire.java.nio.file.Path newBranchPath = ioService.get(new URI(newBranchPathURI));

            baseBranchPath
                .getFileSystem()
                .provider()
                .copy(baseBranchPath,
                      newBranchPath);

            final BranchPermissions branchPermissions = spaceConfigStorageRegistry
                .get(project.getSpace().getName())
                .loadBranchPermissions(baseBranchName,
                                       project.getRepository().getIdentifier());

            spaceConfigStorageRegistry
                .get(project.getSpace().getName())
                .saveBranchPermissions(newBranchName,
                                       project.getRepository().getIdentifier(),
                                       branchPermissions);

            final Repository repository = repositoryService.getRepositoryFromSpace(
                    project.getSpace(),
                    project.getRepository().getAlias());

            repositoryUpdatedEvent.fire(new RepositoryUpdatedEvent(repository));

            newBranchEvent.fire(new NewBranchEvent(repository,
                                                   newBranchName,
                                                   baseBranchName,
                                                   userIdentifier));

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeBranch(final String branchName,
                             final WorkspaceProject project,
                             final String userIdentifier) {

        final Branch branch = project
                .getRepository()
                .getBranch(branchName)
                .orElseThrow(() -> new IllegalStateException("The branch [" + branchName + "] does not exists"));

        try {
            ioService.startBatch(pathUtil.convert(branch.getPath()).getFileSystem());

            repositoryService
                .getRepositoryFromSpace(project.getSpace(),
                                        project.getRepository().getAlias())
                .getBranch(branch.getName())
                .ifPresent(updatedBranch -> {
                        final org.uberfire.java.nio.file.Path branchPath = pathUtil.convert(branch.getPath());

                        ioService.delete(branchPath);

                        spaceConfigStorageRegistry
                            .get(project.getSpace().getName())
                            .deleteBranchPermissions(branch.getName(),
                                                     project.getRepository().getIdentifier());

                        changeRequestService.deleteChangeRequests(project.getSpace().getName(),
                                                                  project.getRepository().getAlias(),
                                                                  branch.getName(),
                                                                  userIdentifier);

                        final Repository repository = repositoryService.getRepositoryFromSpace(
                                project.getSpace(),
                                project.getRepository().getAlias());

                        repositoryUpdatedEvent.fire(new RepositoryUpdatedEvent(repository));
                    });

        } finally {
            ioService.endBatch();
        }
    }
}
