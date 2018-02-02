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

import static org.guvnor.common.services.project.utils.ModuleResourcePaths.POM_PATH;

import java.util.Set;

import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.RenameModuleEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.project.ModuleFactory;
import org.guvnor.common.services.project.service.ModuleServiceCore;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Branch;
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
    protected Event<NewModuleEvent> newModuleEvent;
    protected Event<NewPackageEvent> newPackageEvent;
    protected CommentedOptionFactory commentedOptionFactory;
    protected ResourceResolver resourceResolver;
    protected SessionInfo sessionInfo;
    private ModuleFinder moduleFinder;

    private Event<RenameModuleEvent> renameModuleEvent;
    private Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache;

    protected AbstractModuleService() {
    }

    public AbstractModuleService(final IOService ioService,
                                 final POMService pomService,
                                 final Event<NewModuleEvent> newModuleEvent,
                                 final Event<NewPackageEvent> newPackageEvent,
                                 final Event<RenameModuleEvent> renameModuleEvent,
                                 final Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache,
                                 final SessionInfo sessionInfo,
                                 final CommentedOptionFactory commentedOptionFactory,
                                 final ModuleFinder moduleFinder,
                                 final ResourceResolver resourceResolver) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.newModuleEvent = newModuleEvent;
        this.newPackageEvent = newPackageEvent;
        this.renameModuleEvent = renameModuleEvent;
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
                final Module newModule = resourceResolver.resolveModule(Paths.convert(newModulePath));
                renameModuleEvent.fire(new RenameModuleEvent(oldModule,
                                                             newModule));
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

            final org.uberfire.java.nio.file.Path parentPom = projectDirectory.getParent().resolve(POM_PATH);
            POM parent = null;
            if (ioService.exists(parentPom)) {
                parent = pomService.load(Paths.convert(parentPom));
            }

            ioService.delete(projectDirectory,
                             StandardDeleteOption.NON_EMPTY_DIRECTORIES,
                             commentedOptionFactory.makeCommentedOption(comment));
            //Note we do *not* raise a DeleteModuleEvent here, as that is handled by DeleteModuleObserverBridge

            if (parent != null) {
                parent.setPackaging("pom");
                parent.getModules().remove(module2Delete.getModuleName());
                pomService.save(Paths.convert(parentPom),
                                parent,
                                null,
                                "Removing child module " + module2Delete.getModuleName());
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
}
