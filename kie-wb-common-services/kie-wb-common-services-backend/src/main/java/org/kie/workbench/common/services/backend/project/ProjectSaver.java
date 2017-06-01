/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.utils.ProjectResourcePaths;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;

import static org.guvnor.common.services.project.utils.ProjectResourcePaths.MAIN_RESOURCES_PATH;

public class ProjectSaver {

    private IOService ioService;
    private POMService pomService;
    private KModuleService kModuleService;
    private Event<NewProjectEvent> newProjectEvent;
    private Event<NewPackageEvent> newPackageEvent;
    private KieResourceResolver resourceResolver;
    private ProjectImportsService projectImportsService;
    private ProjectRepositoriesService projectRepositoriesService;
    private PackageNameWhiteListService packageNameWhiteListService;
    private CommentedOptionFactory commentedOptionFactory;
    private SafeSessionInfo safeSessionInfo;

    public ProjectSaver() {
    }

    @Inject
    public ProjectSaver(final @Named("ioStrategy") IOService ioService,
                        final POMService pomService,
                        final KModuleService kModuleService,
                        final Event<NewProjectEvent> newProjectEvent,
                        final Event<NewPackageEvent> newPackageEvent,
                        final KieResourceResolver resourceResolver,
                        final ProjectImportsService projectImportsService,
                        final ProjectRepositoriesService projectRepositoriesService,
                        final PackageNameWhiteListService packageNameWhiteListService,
                        final CommentedOptionFactory commentedOptionFactory,
                        final SessionInfo sessionInfo) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.newProjectEvent = newProjectEvent;
        this.newPackageEvent = newPackageEvent;
        this.resourceResolver = resourceResolver;
        this.projectImportsService = projectImportsService;
        this.projectRepositoriesService = projectRepositoriesService;
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.safeSessionInfo = new SafeSessionInfo(sessionInfo);
    }

    public KieProject save(final Path repositoryRoot,
                           final POM pom,
                           final String baseUrl) {
        try {
            ioService.startBatch(Paths.convert(repositoryRoot).getFileSystem(),
                                 commentedOptionFactory.makeCommentedOption("New project [" + pom.getName() + "]"));

            KieProject kieProject = new NewProjectCreator(pom,
                                                          repositoryRoot).create(baseUrl);

            newProjectEvent.fire(new NewProjectEvent(kieProject,
                                                     safeSessionInfo.getId(),
                                                     safeSessionInfo.getIdentity().getIdentifier()));
            return kieProject;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            ioService.endBatch();
        }
    }

    private class NewProjectCreator {

        private final Path repositoryRoot;
        private final Path projectRootPath;
        private final POM pom;
        private final KieProject simpleProjectInstance;
        private final org.uberfire.java.nio.file.Path projectNioRootPath;

        public NewProjectCreator(final POM pom,
                                 final Path repositoryRoot) {
            this.repositoryRoot = repositoryRoot;
            this.pom = pom;

            String projectName = pom.getName();

            if (projectName == null || projectName.isEmpty()) {
                projectName = pom.getGav().getArtifactId();
            }

            projectNioRootPath = Paths.convert(repositoryRoot).resolve(projectName);

            projectRootPath = Paths.convert(projectNioRootPath);

            simpleProjectInstance = resourceResolver.simpleProjectInstance(Paths.convert(projectRootPath));
        }

        public KieProject create(final String baseUrl) {

            createKieProject(baseUrl);

            return resourceResolver.resolveProject(projectRootPath);
        }

        private Path createKieProject(final String baseUrl) {

            //check if the project already exists.
            checkIfExists();

            // Update parent pom.xml
            updateParentPOM();

            //Create POM.xml
            pomService.create(projectRootPath,
                              baseUrl,
                              pom);

            //Create Maven project structure
            createMavenDirectories();

            //Create a default kmodule.xml
            kModuleService.setUpKModule(simpleProjectInstance.getKModuleXMLPath());

            //Create a default workspace based on the GAV
            createDefaultPackage();

            //Create Project configuration - project imports
            projectImportsService.saveProjectImports(simpleProjectInstance.getImportsPath());

            //Create Project configuration - project package names White List
            packageNameWhiteListService.createProjectWhiteList(simpleProjectInstance.getPackageNamesWhiteListPath());

            //Create Project configuration - Repositories
            projectRepositoriesService.create(simpleProjectInstance.getRepositoriesPath());

            return projectRootPath;
        }

        private void checkIfExists() {
            final org.uberfire.java.nio.file.Path pathToProjectPom = projectNioRootPath.resolve("pom.xml");
            if (ioService.exists(pathToProjectPom)) {
                throw new FileAlreadyExistsException(pathToProjectPom.toString());
            }
        }

        private void updateParentPOM() {
            Path parentPom = Paths.convert(Paths.convert(repositoryRoot).resolve("pom.xml"));
            if (ioService.exists(Paths.convert(parentPom))) {
                POM parent = pomService.load(parentPom);
                parent.setPackaging("pom");
                parent.getModules().add(pom.getName());

                pom.setParent(parent.getGav());

                pomService.save(parentPom,
                                parent,
                                null,
                                "Adding child module " + pom.getName());
            }
        }

        private void createMavenDirectories() {
            ioService.createDirectory(projectNioRootPath.resolve(ProjectResourcePaths.MAIN_SRC_PATH));
            ioService.createDirectory(projectNioRootPath.resolve(ProjectResourcePaths.MAIN_RESOURCES_PATH));
            ioService.createDirectory(projectNioRootPath.resolve(ProjectResourcePaths.TEST_SRC_PATH));
            ioService.createDirectory(projectNioRootPath.resolve(ProjectResourcePaths.TEST_RESOURCES_PATH));
        }

        private void createDefaultPackage() {
            //Raise an event for the new project's default workspace
            newPackageEvent.fire(new NewPackageEvent(resourceResolver.newPackage(getDefaultPackage(),
                                                                                 resourceResolver.getDefaultWorkspacePath(pom.getGav()),
                                                                                 false)));
        }

        private Package getDefaultPackage() {
            return resourceResolver.resolvePackage(Paths.convert(Paths.convert(projectRootPath).resolve(MAIN_RESOURCES_PATH)));
        }
    }
}
