/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import javax.inject.Named;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.api.Remote;
import org.kie.workbench.common.screens.library.api.index.Constants;
import org.kie.workbench.common.services.refactoring.model.index.events.IndexingFinishedEvent;
import org.slf4j.Logger;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.event.BatchIndexEvent;
import org.uberfire.ext.metadata.event.IndexEvent.DeletedEvent;
import org.uberfire.ext.metadata.event.IndexEvent.NewlyIndexedEvent;
import org.uberfire.ext.metadata.event.IndexEvent.RenamedEvent;
import org.uberfire.java.nio.file.api.FileSystemUtils;

@ApplicationScoped
public class LibraryAssetUpdateNotifier {

    private final WorkspaceProjectService projectService;
    private final Event<ProjectAssetListUpdated> assetListUpdateEvent;
    private final LibraryIndexer libraryIndexer;
    private final Logger logger;

    // For proxying
    public LibraryAssetUpdateNotifier() {
        this(null, null, null, null);
    }

    @Inject
    public LibraryAssetUpdateNotifier(final WorkspaceProjectService projectService,
                                      final LibraryIndexer libraryIndexer,
                                      final @Remote Event<ProjectAssetListUpdated> assetListUpdateEvent,
                                      final Logger logger) {
        this.projectService = projectService;
        this.libraryIndexer = libraryIndexer;
        this.assetListUpdateEvent = assetListUpdateEvent;
        this.logger = logger;
    }

    public void notifyOnUpdatedAssets(@Observes @Named(Constants.INDEXER_ID) BatchIndexEvent event) {

        if (!isUpdateNotifierEnabled()) {
            return;
        }

        // Assume that all indexed items are from the same project.
        event.getIndexEvents()
                .stream()
                .flatMap(evt -> {
                    switch (evt.getKind()) {
                        case Deleted:
                            return Stream.of(((DeletedEvent) evt).getDeleted().getKey());
                        case NewlyIndexed:
                            return Stream.of(((NewlyIndexedEvent) evt).getKObject().getKey());
                        case Renamed:
                            return Stream.of(((RenamedEvent) evt).getTarget().getKey());
                        default:
                            return Stream.empty();
                    }
                })
                .map(path -> org.uberfire.java.nio.file.Paths.get(path))
                .filter(path -> libraryIndexer.supportsPath(path))
                .flatMap(path -> {
                    try {
                        WorkspaceProject project = projectService.resolveProject(Paths.convert(path));
                        return (project == null) ? Stream.empty() : Stream.of(project);
                    } catch (Throwable t) {
                        return Stream.empty();
                    }
                })
                .map(project -> new ProjectAssetListUpdated(project))
                .findFirst()
                .ifPresent(clientEvent -> {
                    logger.info("Sending indexing notification for project [{}].", clientEvent.getProject().getRepository().getIdentifier());
                    assetListUpdateEvent.fire(clientEvent);
                });
    }

    private void onProjectIndexingFinishedEvent(@Observes IndexingFinishedEvent event) {

        if (!isUpdateNotifierEnabled()) {
            return;
        }

        WorkspaceProject project = projectService.resolveProject(event.getPath());

        if (project == null) {
            throw new IllegalStateException("Cannot resolve Project for KClusterId: '" + event.getkClusterId() + "' and path: '" + event.getPath().toString() + "'.");
        }

        assetListUpdateEvent.fire(new ProjectAssetListUpdated(project));
    }

    protected boolean isUpdateNotifierEnabled() {
        return FileSystemUtils.isGitDefaultFileSystem();
    }
}
