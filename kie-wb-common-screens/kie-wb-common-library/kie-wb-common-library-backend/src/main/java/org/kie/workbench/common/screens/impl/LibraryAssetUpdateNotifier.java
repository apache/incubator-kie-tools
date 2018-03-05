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

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.api.Remote;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.event.BatchIndexEvent;

@ApplicationScoped
public class LibraryAssetUpdateNotifier {

    private final WorkspaceProjectService projectService;
    private final Event<ProjectAssetListUpdated> assetListUpdateEvent;
    private final LibraryIndexer libraryIndexer;

    // For proxying
    public LibraryAssetUpdateNotifier() {
        this(null, null, null);
    }

    @Inject
    public LibraryAssetUpdateNotifier(final WorkspaceProjectService projectService,
                                      final LibraryIndexer libraryIndexer,
                                      final @Remote Event<ProjectAssetListUpdated> assetListUpdateEvent) {
        this.projectService = projectService;
        this.libraryIndexer = libraryIndexer;
        this.assetListUpdateEvent = assetListUpdateEvent;
    }

    public void notifyOnUpdatedAssets(@Observes BatchIndexEvent event) {
        // Assume that all indexed items are from the same project.
        event.getIndexed()
             .stream()
             .map(kobject -> kobject.getKey())
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
             .ifPresent(clientEvent -> assetListUpdateEvent.fire(clientEvent));
    }

}
