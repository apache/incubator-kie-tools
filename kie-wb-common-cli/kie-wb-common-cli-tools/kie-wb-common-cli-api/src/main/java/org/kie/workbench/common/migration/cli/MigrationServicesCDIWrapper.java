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
 */
package org.kie.workbench.common.migration.cli;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class MigrationServicesCDIWrapper {

    private IOService ioService;

    private CommentedOptionFactory commentedOptionFactory;

    private Repository systemRepository;

    private IOService systemIoService;

    private WorkspaceProjectService workspaceProjectService;

    @Inject
    public MigrationServicesCDIWrapper(final @Named("ioStrategy") IOService ioService,
                                       final CommentedOptionFactory commentedOptionFactory,
                                       final @Named("system") Repository systemRepository,
                                       final @Named("configIO") IOService systemIoService,
                                       final WorkspaceProjectService workspaceProjectService) {
        this.ioService = ioService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.systemRepository = systemRepository;
        this.systemIoService = systemIoService;
        this.workspaceProjectService = workspaceProjectService;
    }

    public IOService getIOService() {
        return ioService;
    }

    public void write(Path path, String content, String comment) {
        ioService.write(Paths.convert(path), content, commentedOptionFactory.makeCommentedOption(comment));
    }

    public Repository getSystemRepository() {
        return systemRepository;
    }

    public IOService getSystemIoService() {
        return systemIoService;
    }

    public WorkspaceProjectService getWorkspaceProjectService() {
        return workspaceProjectService;
    }
}
