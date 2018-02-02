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

package org.guvnor.common.services.project.backend.server;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

public abstract class AbstractModuleRepositoriesServiceImpl<T extends Module>
        implements ModuleRepositoriesService {

    protected IOService ioService;
    protected ModuleRepositoryResolver repositoryResolver;
    protected ModuleRepositoriesContentHandler contentHandler;
    protected CommentedOptionFactory commentedOptionFactory;

    public AbstractModuleRepositoriesServiceImpl() {
        //WELD proxy
    }

    public AbstractModuleRepositoriesServiceImpl(final IOService ioService,
                                                 final ModuleRepositoryResolver repositoryResolver,
                                                 final ModuleRepositoriesContentHandler contentHandler,
                                                 final CommentedOptionFactory commentedOptionFactory) {
        this.ioService = ioService;
        this.repositoryResolver = repositoryResolver;
        this.contentHandler = contentHandler;
        this.commentedOptionFactory = commentedOptionFactory;
    }

    @Override
    public ModuleRepositories create(final Path path) {
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        if (ioService.exists(nioPath)) {
            throw new FileAlreadyExistsException(path.toString());
        }

        try {
            ioService.startBatch(nioPath.getFileSystem(),
                                 commentedOptionFactory.makeCommentedOption("Creating " + path.toString() + "..."));

            final T project = getModule(path);
            final Set<MavenRepositoryMetadata> content = new HashSet<MavenRepositoryMetadata>();
            if (project == null) {
                content.addAll(repositoryResolver.getRemoteRepositoriesMetaData());
            } else {
                content.addAll(repositoryResolver.getRemoteRepositoriesMetaData(project));
            }

            final ModuleRepositories repositories = createProjectRepositories(content);
            ioService.write(Paths.convert(path),
                            contentHandler.toString(repositories));

            return repositories;
        } finally {
            ioService.endBatch();
        }
    }

    private ModuleRepositories createProjectRepositories(final Set<MavenRepositoryMetadata> content) {
        final Set<ModuleRepositories.ModuleRepository> projectRepositories = new HashSet<ModuleRepositories.ModuleRepository>();
        for (MavenRepositoryMetadata md : content) {
            projectRepositories.add(new ModuleRepositories.ModuleRepository(true,
                                                                            md));
        }
        final ModuleRepositories repositories = new ModuleRepositories(projectRepositories);
        return repositories;
    }

    @Override
    public ModuleRepositories load(final Path path) {
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        if (ioService.exists(nioPath)) {
            final String content = ioService.readAllString(nioPath);
            return contentHandler.toModel(content);
        } else {
            return create(path);
        }
    }

    @Override
    public Path save(final Path resource,
                     final ModuleRepositories moduleRepositories,
                     final String comment) {
        try {
            ioService.write(Paths.convert(resource),
                            contentHandler.toString(moduleRepositories));
            return resource;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    protected abstract T getModule(final Path path);
}
