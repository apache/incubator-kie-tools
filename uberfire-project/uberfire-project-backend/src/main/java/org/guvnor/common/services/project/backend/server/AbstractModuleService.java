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

import static java.util.Optional.ofNullable;
import static org.guvnor.common.services.project.utils.ModuleResourcePaths.POM_PATH;

import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.project.ModuleFactory;
import org.guvnor.common.services.project.service.ModuleServiceCore;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.utils.ModuleResourcePaths;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.rpc.SessionInfo;

public abstract class AbstractModuleService<T extends Module>
        implements ModuleServiceCore<T>,
                   ModuleFactory<T> {

    protected IOService ioService;
    protected POMService pomService;
    private RepositoryService repoService;
    protected Event<NewModuleEvent> newModuleEvent;
    protected Event<NewPackageEvent> newPackageEvent;
    protected CommentedOptionFactory commentedOptionFactory;
    protected ResourceResolver resourceResolver;
    protected SessionInfo sessionInfo;
    private ModuleFinder moduleFinder;

    private Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache;

    protected AbstractModuleService() {
    }

    public AbstractModuleService(final IOService ioService,
                                 final POMService pomService,
                                 final RepositoryService repoService,
                                 final Event<NewModuleEvent> newModuleEvent,
                                 final Event<NewPackageEvent> newPackageEvent,
                                 final Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache,
                                 final SessionInfo sessionInfo,
                                 final CommentedOptionFactory commentedOptionFactory,
                                 final ModuleFinder moduleFinder,
                                 final ResourceResolver resourceResolver) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.repoService = repoService;
        this.newModuleEvent = newModuleEvent;
        this.newPackageEvent = newPackageEvent;
        this.invalidateDMOCache = invalidateDMOCache;
        this.commentedOptionFactory = commentedOptionFactory;
        this.moduleFinder = moduleFinder;
        this.resourceResolver = resourceResolver;
        this.sessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @Override
    public Set<Module> getAllModules(final Branch branch) {
        return moduleFinder.find(resourceResolver,
         branch);
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

    @Override
    public Path rename(final Path pathToPomXML,
                       final String newName,
                       final String comment) {

        try {
            final org.uberfire.java.nio.file.Path projectDirectory = Paths.convert(pathToPomXML).getParent();
            final org.uberfire.java.nio.file.Path newModulePath = projectDirectory.resolveSibling(newName);

            final POM content = pomService.load(pathToPomXML);

            if (newModulePath.equals(projectDirectory)) {
                return pathToPomXML;
            }

            if (ioService.exists(newModulePath)) {
                throw new FileAlreadyExistsException(newModulePath.toString());
            }

            final Path oldModuleDir = Paths.convert(projectDirectory);
            final Module oldModule = resourceResolver.resolveModule(oldModuleDir);

            content.setName(newName);
            final Path newPathToPomXML = Paths.convert(newModulePath.resolve(POM_PATH));
            try {
                ioService.startBatch(newModulePath.getFileSystem());
                ioService.move(projectDirectory,
                               newModulePath,
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

            invalidateDMOCache.fire(new InvalidateDMOModuleCacheEvent(sessionInfo,
                                                                      oldModule,
                                                                      oldModuleDir));

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
            final Module module2Delete = resourceResolver.resolveModule(Paths.convert(projectDirectory));

            final Optional<org.uberfire.java.nio.file.Path> parentPomPath =
                    ofNullable(projectDirectory).flatMap(dir -> ofNullable(dir.getParent()))
                                                .map(dir -> dir.resolve(POM_PATH))
                                                .filter(pom -> ioService.exists(pom));
            final Optional<POM> parentPom = parentPomPath.map(pom -> pomService.load(Paths.convert(pom)));


            // If this is the top module of the project, we should delete the whole repository.
            if (parentPomPath.isPresent() && parentPom.isPresent()) {
                //Note we do *not* raise a DeleteModuleEvent here, as that is handled by DeleteModuleObserverBridge
                ioService.delete(projectDirectory,
                                 StandardDeleteOption.NON_EMPTY_DIRECTORIES,
                                 commentedOptionFactory.makeCommentedOption(comment));

                org.uberfire.java.nio.file.Path pomPath = parentPomPath.get();
                POM pom = parentPom.get();
                pom.setPackaging("pom");
                pom.getModules().remove(module2Delete.getModuleName());
                pomService.save(Paths.convert(pomPath),
                                pom,
                                null,
                                "Removing child module " + module2Delete.getModuleName());
            } else {
                Repository repo = repoService.getRepository(Paths.convert(projectDirectory));
                repoService.removeRepository(repo.getSpace(), repo.getAlias());
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
            final org.uberfire.java.nio.file.Path newModulePath = projectDirectory.resolveSibling(newName);

            final POM content = pomService.load(pathToPomXML);

            if (newModulePath.equals(projectDirectory)) {
                return;
            }

            if (ioService.exists(newModulePath)) {
                throw new FileAlreadyExistsException(newModulePath.toString());
            }

            content.setName(newName);
            final Path newPathToPomXML = Paths.convert(newModulePath.resolve(POM_PATH));
            try {
                ioService.startBatch(newModulePath.getFileSystem());
                ioService.copy(projectDirectory,
                               newModulePath,
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
            final Module newModule = resourceResolver.resolveModule(Paths.convert(newModulePath));
            newModuleEvent.fire(new NewModuleEvent(newModule,
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
            final Module module = resourceResolver.resolveModule(path);

            invalidateDMOCache.fire(new InvalidateDMOModuleCacheEvent(sessionInfo,
                                                                      module,
                                                                      path));
        } catch (final Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void createModuleDirectories(final Path repositoryRoot) {
        final org.uberfire.java.nio.file.Path modulePath = Paths.convert(repositoryRoot);

        try {
            ioService.startBatch(modulePath.getFileSystem(),
                                 commentedOptionFactory.makeCommentedOption("Adding module directories"));

            final Path mainResourcesPath = Paths.convert(modulePath.resolve(ModuleResourcePaths.MAIN_RESOURCES_PATH));
            final Package defaultPackage = resourceResolver.resolvePackage(mainResourcesPath);

            final Path pomPath = Paths.convert(modulePath.resolve(POMServiceImpl.POM_XML));
            final POM modulePom = pomService.load(pomPath);
            final String workspacePath = resourceResolver.getDefaultWorkspacePath(modulePom.getGav());

            resourceResolver.newPackage(defaultPackage,
                                        workspacePath,
                                        false);
        } finally {
            ioService.endBatch();
        }
    }
}
