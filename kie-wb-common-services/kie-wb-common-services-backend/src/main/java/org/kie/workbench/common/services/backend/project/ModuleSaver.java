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
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.utils.ModuleResourcePaths;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;

import static org.guvnor.common.services.project.utils.ModuleResourcePaths.MAIN_RESOURCES_PATH;

public class ModuleSaver {

    private IOService ioService;
    private POMService pomService;
    private KModuleService kModuleService;
    private Event<NewModuleEvent> newModuleEvent;
    private Event<NewPackageEvent> newPackageEvent;
    private KieResourceResolver resourceResolver;
    private ProjectImportsService projectImportsService;
    private ModuleRepositoriesService moduleRepositoriesService;
    private PackageNameWhiteListService packageNameWhiteListService;
    private CommentedOptionFactory commentedOptionFactory;
    private SafeSessionInfo safeSessionInfo;

    public ModuleSaver() {
    }

    @Inject
    public ModuleSaver(final @Named("ioStrategy") IOService ioService,
                       final POMService pomService,
                       final KModuleService kModuleService,
                       final Event<NewModuleEvent> newModuleEvent,
                       final Event<NewPackageEvent> newPackageEvent,
                       final KieResourceResolver resourceResolver,
                       final ProjectImportsService projectImportsService,
                       final ModuleRepositoriesService moduleRepositoriesService,
                       final PackageNameWhiteListService packageNameWhiteListService,
                       final CommentedOptionFactory commentedOptionFactory,
                       final SessionInfo sessionInfo) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.newModuleEvent = newModuleEvent;
        this.newPackageEvent = newPackageEvent;
        this.resourceResolver = resourceResolver;
        this.projectImportsService = projectImportsService;
        this.moduleRepositoriesService = moduleRepositoriesService;
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.safeSessionInfo = new SafeSessionInfo(sessionInfo);
    }

    public KieModule save(final Path repositoryRoot,
                          final POM pom) {
        try {
            ioService.startBatch(Paths.convert(repositoryRoot).getFileSystem(),
                                 commentedOptionFactory.makeCommentedOption("New module [" + pom.getName() + "]"));

            KieModule kieModule = createNewModuleCreator(repositoryRoot, pom).create();

            newModuleEvent.fire(new NewModuleEvent(kieModule,
                                                   safeSessionInfo.getId(),
                                                   safeSessionInfo.getIdentity().getIdentifier()));
            return kieModule;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            ioService.endBatch();
        }
    }

    public NewModuleCreator createNewModuleCreator(final Path repositoryRoot, final POM pom) {
        return new NewModuleCreator(pom,repositoryRoot);
    }

    protected class NewModuleCreator {

        private final Path moduleRoot;
        private final POM pom;
        private final KieModule simpleModuleInstance;

        public NewModuleCreator(final POM pom,
                                final Path moduleRoot) {
            this.moduleRoot = moduleRoot;
            this.pom = pom;

            simpleModuleInstance = resourceResolver.simpleModuleInstance(Paths.convert(moduleRoot));
        }

        public KieModule create() {

            createModule();

            return resourceResolver.resolveModule(moduleRoot);
        }

        private void createModule() {

            //check if the module already exists.
            checkIfExists();

            // Update parent pom.xml
            updateParentPOM();

            //Create POM.xml
            pomService.create(moduleRoot,
                              pom);

            //Create Maven module structure
            createMavenDirectories();

            //Create a default kmodule.xml
            kModuleService.setUpKModule(simpleModuleInstance.getKModuleXMLPath());

            //Create a default workspace based on the GAV
            createDefaultPackage();

            //Create Module configuration - project imports
            projectImportsService.saveProjectImports(simpleModuleInstance.getImportsPath());

            //Create Module configuration - project package names White List
            String packageNamesWhiteListContent = defaultPackageNamesWhiteListEntry();
            packageNameWhiteListService.createModuleWhiteList(simpleModuleInstance.getPackageNamesWhiteListPath(), packageNamesWhiteListContent);

            //Create Module configuration - Repositories
            moduleRepositoriesService.create(simpleModuleInstance.getRepositoriesPath());
        }

        private void checkIfExists() {
            final org.uberfire.java.nio.file.Path pathToModulePom = Paths.convert(moduleRoot).resolve("pom.xml");
            if (ioService.exists(pathToModulePom)) {
                throw new FileAlreadyExistsException(pathToModulePom.toString());
            }
        }

        private void updateParentPOM() {
            Path parentPom = Paths.convert(Paths.convert(moduleRoot).resolve("pom.xml"));
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
            ioService.createDirectory(Paths.convert(moduleRoot).resolve(ModuleResourcePaths.MAIN_SRC_PATH));
            ioService.createDirectory(Paths.convert(moduleRoot).resolve(ModuleResourcePaths.MAIN_RESOURCES_PATH));
            ioService.createDirectory(Paths.convert(moduleRoot).resolve(ModuleResourcePaths.TEST_SRC_PATH));
            ioService.createDirectory(Paths.convert(moduleRoot).resolve(ModuleResourcePaths.TEST_RESOURCES_PATH));
        }

        private void createDefaultPackage() {
            //Raise an event for the new module's default workspace
            newPackageEvent.fire(new NewPackageEvent(resourceResolver.newPackage(getDefaultPackage(),
                                                                                 resourceResolver.getDefaultWorkspacePath(pom.getGav()),
                                                                                 false)));
        }

        private Package getDefaultPackage() {
            return resourceResolver.resolvePackage(Paths.convert(Paths.convert(moduleRoot).resolve(MAIN_RESOURCES_PATH)));
        }
        
        public String defaultPackageNamesWhiteListEntry() {
            return String.join(".", pom.getGav().getGroupId(), "**");
        }
    }
}
