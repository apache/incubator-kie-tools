/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.project.ProjectFactory;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectServiceCore;
import org.guvnor.common.services.workingset.client.model.WorkingSetSettings;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.guvnor.common.services.project.utils.ProjectResourcePaths.POM_PATH;

public abstract class AbstractProjectService<T extends Project>
        implements ProjectServiceCore<T>,
                   ProjectFactory<T> {

    protected IOService ioService;
    protected POMService pomService;

    private ConfigurationService configurationService;
    private ConfigurationFactory configurationFactory;

    protected Event<NewProjectEvent> newProjectEvent;
    protected Event<NewPackageEvent> newPackageEvent;

    private Event<RenameProjectEvent> renameProjectEvent;

    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache;

    private AuthorizationManager authorizationManager;

    private BackwardCompatibleUtil backward;

    protected CommentedOptionFactory commentedOptionFactory;
    protected ResourceResolver resourceResolver;

    protected SessionInfo sessionInfo;

    protected AbstractProjectService() {
    }

    public AbstractProjectService(final IOService ioService,
                                  final POMService pomService,
                                  final ConfigurationService configurationService,
                                  final ConfigurationFactory configurationFactory,
                                  final Event<NewProjectEvent> newProjectEvent,
                                  final Event<NewPackageEvent> newPackageEvent,
                                  final Event<RenameProjectEvent> renameProjectEvent,
                                  final Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache,
                                  final SessionInfo sessionInfo,
                                  final AuthorizationManager authorizationManager,
                                  final BackwardCompatibleUtil backward,
                                  final CommentedOptionFactory commentedOptionFactory,
                                  final ResourceResolver resourceResolver) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.configurationService = configurationService;
        this.configurationFactory = configurationFactory;
        this.newProjectEvent = newProjectEvent;
        this.newPackageEvent = newPackageEvent;
        this.renameProjectEvent = renameProjectEvent;
        this.invalidateDMOCache = invalidateDMOCache;
        this.authorizationManager = authorizationManager;
        this.backward = backward;
        this.commentedOptionFactory = commentedOptionFactory;
        this.resourceResolver = resourceResolver;
        this.sessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig(final Path project) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public Set<Project> getAllProjects(Repository repository,
                                       String branch) {
        return getProjects(repository,
                           branch,
                           false);
    }

    @Override
    public Set<Project> getProjects(final Repository repository,
                                    String branch) {
        return getProjects(repository,
                           branch,
                           true);
    }

    public Set<Project> getProjects(final Repository repository,
                                    String branch,
                                    boolean secure) {
        final Set<Project> authorizedProjects = new HashSet<Project>();
        if (repository == null) {
            return authorizedProjects;
        }
        final Path repositoryRoot = repository.getBranchRoot(branch);
        final DirectoryStream<org.uberfire.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream(Paths.convert(repositoryRoot));
        try {
            for (org.uberfire.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths) {
                if (Files.isDirectory(nioRepositoryPath)) {
                    final org.uberfire.backend.vfs.Path projectPath = Paths.convert(nioRepositoryPath);
                    final Project project = resourceResolver.resolveProject(projectPath);

                    if (project != null) {
                        if (!secure || authorizationManager.authorize(project,
                                                                      sessionInfo.getIdentity())) {
                            POM projectPom = pomService.load(project.getPomXMLPath());
                            project.setPom(projectPom);
                            authorizedProjects.add(project);
                        }
                    }
                }
            }
        } finally {
            nioRepositoryPaths.close();
        }
        return authorizedProjects;
    }

    @Override
    public Package newPackage(final Package parentPackage,
                              final String packageName) {
        try {
            //Make new Package
            final Package newPackage = resourceResolver.newPackage(parentPackage,
                                                                   packageName,
                                                                   true);

            //Raise an event for the new package
            newPackageEvent.fire(new NewPackageEvent(newPackage));

            //Return the new package
            return newPackage;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public void addGroup(final Project project,
                         final String group) {
        ConfigGroup thisProjectConfig = resourceResolver.findProjectConfig(project.getRootPath());

        if (thisProjectConfig == null) {
            thisProjectConfig = configurationFactory.newConfigGroup(ConfigType.PROJECT,
                                                                    project.getRootPath().toURI(),
                                                                    "Project '" + project.getProjectName() + "' configuration");
            thisProjectConfig.addConfigItem(configurationFactory.newConfigItem("security:groups",
                                                                               new ArrayList<String>()));
            configurationService.addConfiguration(thisProjectConfig);
        }

        if (thisProjectConfig != null) {
            final ConfigItem<List> groups = backward.compat(thisProjectConfig).getConfigItem("security:groups");
            groups.getValue().add(group);

            configurationService.updateConfiguration(thisProjectConfig);
        } else {
            throw new IllegalArgumentException("Project " + project.getProjectName() + " not found");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void removeGroup(final Project project,
                            final String group) {
        final ConfigGroup thisProjectConfig = resourceResolver.findProjectConfig(project.getRootPath());

        if (thisProjectConfig != null) {
            final ConfigItem<List> groups = backward.compat(thisProjectConfig).getConfigItem("security:groups");
            groups.getValue().remove(group);

            configurationService.updateConfiguration(thisProjectConfig);
        } else {
            throw new IllegalArgumentException("Project " + project.getProjectName() + " not found");
        }
    }

    @Override
    public Path rename(final Path pathToPomXML,
                       final String newName,
                       final String comment) {

        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert(pathToPomXML).getParent();
            final org.uberfire.java.nio.file.Path newProjectPath = projectDirectory.resolveSibling(newName);

            final POM content = pomService.load(pathToPomXML);

            if (newProjectPath.equals(projectDirectory)) {
                return pathToPomXML;
            }

            if (ioService.exists(newProjectPath)) {
                throw new FileAlreadyExistsException(newProjectPath.toString());
            }

            final Path oldProjectDir = Paths.convert(projectDirectory);
            final Project oldProject = resourceResolver.resolveProject(oldProjectDir);

            content.setName(newName);
            final Path newPathToPomXML = Paths.convert(newProjectPath.resolve(POM_PATH));
            try {
                ioService.startBatch(newProjectPath.getFileSystem());
                ioService.move(projectDirectory,
                               newProjectPath,
                               commentedOptionFactory.makeCommentedOption(comment));
                pomService.save(newPathToPomXML,
                                content,
                                null,
                                comment);
            } catch (final Exception e) {
                throw e;
            } finally {
                final Project newProject = resourceResolver.resolveProject(Paths.convert(newProjectPath));
                renameProjectEvent.fire(new RenameProjectEvent(oldProject,
                                                               newProject));
                ioService.endBatch();
            }

            invalidateDMOCache.fire(new InvalidateDMOProjectCacheEvent(sessionInfo,
                                                                       oldProject,
                                                                       oldProjectDir));

            return newPathToPomXML;
        } catch (final Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void delete(final Path pathToPomXML,
                       final String comment) {
        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert(pathToPomXML).getParent();
            final Project project2Delete = resourceResolver.resolveProject(Paths.convert(projectDirectory));

            final org.uberfire.java.nio.file.Path parentPom = projectDirectory.getParent().resolve(POM_PATH);
            POM parent = null;
            if (ioService.exists(parentPom)) {
                parent = pomService.load(Paths.convert(parentPom));
            }

            ioService.delete(projectDirectory,
                             StandardDeleteOption.NON_EMPTY_DIRECTORIES,
                             commentedOptionFactory.makeCommentedOption(comment));
            //Note we do *not* raise a DeleteProjectEvent here, as that is handled by DeleteProjectObserverBridge

            if (parent != null) {
                parent.setPackaging("pom");
                parent.getModules().remove(project2Delete.getProjectName());
                pomService.save(Paths.convert(parentPom),
                                parent,
                                null,
                                "Removing child module " + project2Delete.getProjectName());
            }
        } catch (final Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void copy(final Path pathToPomXML,
                     final String newName,
                     final String comment) {
        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert(pathToPomXML).getParent();
            final org.uberfire.java.nio.file.Path newProjectPath = projectDirectory.resolveSibling(newName);

            final POM content = pomService.load(pathToPomXML);

            if (newProjectPath.equals(projectDirectory)) {
                return;
            }

            if (ioService.exists(newProjectPath)) {
                throw new FileAlreadyExistsException(newProjectPath.toString());
            }

            content.setName(newName);
            final Path newPathToPomXML = Paths.convert(newProjectPath.resolve(POM_PATH));
            try {
                ioService.startBatch(newProjectPath.getFileSystem());
                ioService.copy(projectDirectory,
                               newProjectPath,
                               commentedOptionFactory.makeCommentedOption(comment));
                pomService.save(newPathToPomXML,
                                content,
                                null,
                                comment);
            } catch (final Exception e) {
                throw e;
            } finally {
                ioService.endBatch();
            }
            final Project newProject = resourceResolver.resolveProject(Paths.convert(newProjectPath));
            newProjectEvent.fire(new NewProjectEvent(newProject,
                                                     commentedOptionFactory.getSafeSessionId(),
                                                     commentedOptionFactory.getSafeIdentityName()));
        } catch (final Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void reImport(final Path pathToPomXML) {

        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert(pathToPomXML).getParent();
            final Path path = Paths.convert(projectDirectory);
            final Project project = resourceResolver.resolveProject(path);

            invalidateDMOCache.fire(new InvalidateDMOProjectCacheEvent(sessionInfo,
                                                                       project,
                                                                       path));
        } catch (final Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }
}
