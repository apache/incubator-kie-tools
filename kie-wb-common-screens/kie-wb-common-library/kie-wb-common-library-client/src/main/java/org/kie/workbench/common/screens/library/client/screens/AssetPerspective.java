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

package org.kie.workbench.common.screens.library.client.screens;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryBreadcrumbs;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.ext.editor.commons.client.event.ConcurrentDeleteAcceptedEvent;
import org.uberfire.ext.editor.commons.client.event.ConcurrentRenameAcceptedEvent;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective( identifier = LibraryPlaces.ASSET_PERSPECTIVE )
public class AssetPerspective {

    private PlaceManager placeManager;

    private LibraryBreadcrumbs libraryBreadcrumbs;

    private Event<ProjectDetailEvent> projectDetailEvent;

    private Event<AssetDetailEvent> assetDetailEvent;

    Path path;

    Project project;

    @Inject
    public AssetPerspective( final PlaceManager placeManager,
                             final LibraryBreadcrumbs libraryBreadcrumbs,
                             final Event<ProjectDetailEvent> projectDetailEvent,
                             final Event<AssetDetailEvent> assetDetailEvent ) {
        this.placeManager = placeManager;
        this.libraryBreadcrumbs = libraryBreadcrumbs;
        this.projectDetailEvent = projectDetailEvent;
        this.assetDetailEvent = assetDetailEvent;
    }

    public void assetSelected( @Observes final AssetDetailEvent assetDetails ) {
        final PathPlaceRequest pathPlaceRequest = generatePathPlaceRequest( assetDetails.getPath() );
        path = pathPlaceRequest.getPath();
        project = assetDetails.getProject();

        pathPlaceRequest.getPath().onRename( () -> updateBreadcrumbs( project, path ) );
        pathPlaceRequest.getPath().onDelete( () -> goBackToProject( project ) );

        placeManager.goTo( pathPlaceRequest );
        updateBreadcrumbs( project, pathPlaceRequest.getPath() );
    }

    public void assetDeletedAccepted( @Observes final ConcurrentDeleteAcceptedEvent concurrentDeleteAcceptedEvent ) {
        if ( path != null && path.equals( concurrentDeleteAcceptedEvent.getPath() ) ) {
            goBackToProject( project );
        }
    }

    public void assetRenamedAccepted( @Observes final ConcurrentRenameAcceptedEvent concurrentRenameAcceptedEvent ) {
        if ( path != null && path.equals( concurrentRenameAcceptedEvent.getPath() ) ) {
            updateBreadcrumbs( project, path );
        }
    }

    private void updateBreadcrumbs( final Project project,
                                    final Path path ) {
        libraryBreadcrumbs.setupLibraryBreadCrumbsForAsset( project,
                                                            path );
    }

    private void goBackToProject( final Project project ) {
        placeManager.goTo( LibraryPlaces.PROJECT_SCREEN );
        projectDetailEvent.fire( new ProjectDetailEvent( project ) );
    }

    PathPlaceRequest generatePathPlaceRequest( final Path path ) {
        return new PathPlaceRequest( path );
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        p.setName( "Asset Perspective" );

        return p;
    }
}
