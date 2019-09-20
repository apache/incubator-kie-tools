/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.impl;

import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.kie.workbench.common.screens.library.api.RepositoryFileListUpdatedEvent;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.event.BatchIndexEvent;
import org.uberfire.ext.metadata.event.IndexEvent;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

@ApplicationScoped
public class LibraryFileUpdateNotifier {

    private final WorkspaceProjectService workspaceProjectService;
    private final Event<RepositoryFileListUpdatedEvent> repositoryFileListUpdatedEvent;

    @Inject
    public LibraryFileUpdateNotifier(final WorkspaceProjectService workspaceProjectService,
                                     final Event<RepositoryFileListUpdatedEvent> repositoryFileListUpdatedEvent) {
        this.workspaceProjectService = workspaceProjectService;
        this.repositoryFileListUpdatedEvent = repositoryFileListUpdatedEvent;
    }

    public void onBatchIndexEvent(@Observes final BatchIndexEvent event) {
        event.getIndexEvents()
                .stream()
                .flatMap(evt -> {
                    switch (evt.getKind()) {
                        case Deleted:
                            return Stream.of(((IndexEvent.DeletedEvent) evt).getDeleted().getKey());
                        case NewlyIndexed:
                            return Stream.of(((IndexEvent.NewlyIndexedEvent) evt).getKObject().getKey());
                        case Renamed:
                            return Stream.of(((IndexEvent.RenamedEvent) evt).getTarget().getKey());
                        default:
                            return Stream.empty();
                    }
                })
                .map(pathStr -> {
                    final Path path = getPath(pathStr);
                    final WorkspaceProject project = workspaceProjectService.resolveProject(convertPath(path));
                    final String repositoryId = project.getRepository().getIdentifier();

                    final String branchName = (path instanceof JGitPathImpl) ?
                            ((JGitPathImpl) path).getRefTree() : null;

                    return new RepositoryFileListUpdatedEvent(repositoryId,
                                                              branchName);
                })
                .distinct()
                .forEach(repositoryFileListUpdatedEvent::fire);
    }

    Path getPath(final String pathStr) {
        return org.uberfire.java.nio.file.Paths.get(pathStr);
    }

    org.uberfire.backend.vfs.Path convertPath(final Path path) {
        return Paths.convert(path);
    }
}
