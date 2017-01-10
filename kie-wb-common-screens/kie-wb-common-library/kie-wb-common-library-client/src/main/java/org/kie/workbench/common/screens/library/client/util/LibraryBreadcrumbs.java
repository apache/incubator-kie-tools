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
package org.kie.workbench.common.screens.library.client.util;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.LibraryBreadCrumbToolbarPresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class LibraryBreadcrumbs {

    private UberfireBreadcrumbs breadcrumbs;

    private TranslationService ts;

    private Event<ProjectDetailEvent> projectDetailEvent;

    private Event<AssetDetailEvent> assetDetailEvent;

    private ResourceUtils resourceUtils;

    @Inject
    public LibraryBreadcrumbs( final UberfireBreadcrumbs breadcrumbs,
                               final TranslationService ts,
                               final Event<ProjectDetailEvent> projectDetailEvent,
                               final Event<AssetDetailEvent> assetDetailEvent,
                               final ResourceUtils resourceUtils ) {
        this.breadcrumbs = breadcrumbs;
        this.ts = ts;
        this.projectDetailEvent = projectDetailEvent;
        this.assetDetailEvent = assetDetailEvent;
        this.resourceUtils = resourceUtils;
    }

    public LibraryBreadcrumbs() {
    }

    public void setupToolBar( final LibraryBreadCrumbToolbarPresenter breadCrumbToolbarPresenter ) {
        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.LIBRARY_PERSPECTIVE );
        breadcrumbs.addBreadCrumb( LibraryPlaces.LIBRARY_PERSPECTIVE, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_SCREEN ) );
        breadcrumbs.addToolbar( LibraryPlaces.LIBRARY_PERSPECTIVE, breadCrumbToolbarPresenter.getView().getElement() );
    }

    public void setupAuthoringBreadCrumbsForProject( final String projectName ) {
        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.AUTHORING );
        breadcrumbs.addBreadCrumb( LibraryPlaces.AUTHORING, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_SCREEN ) );
        breadcrumbs.addBreadCrumb( LibraryPlaces.AUTHORING, projectName,
                                   new DefaultPlaceRequest( LibraryPlaces.AUTHORING ) );
    }

    public void setupAuthoringBreadcrumbsForExample() {
        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.AUTHORING );
        breadcrumbs.addBreadCrumb( LibraryPlaces.AUTHORING, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_SCREEN ) );
    }

    public void setupLibraryBreadCrumbs() {
        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.LIBRARY_PERSPECTIVE );
        breadcrumbs.addBreadCrumb( LibraryPlaces.LIBRARY_PERSPECTIVE, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_SCREEN ) );
    }

    public void setupLibraryBreadCrumbsForProject( final Project project ) {
        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.LIBRARY_PERSPECTIVE );
        breadcrumbs.addBreadCrumb( LibraryPlaces.LIBRARY_PERSPECTIVE,
                                   ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_SCREEN ) );
        breadcrumbs.addBreadCrumb( LibraryPlaces.LIBRARY_PERSPECTIVE,
                                   project.getProjectName(),
                                   new DefaultPlaceRequest( LibraryPlaces.PROJECT_SCREEN ),
                                   () -> {
                                       projectDetailEvent.fire( new ProjectDetailEvent( project ) );
                                   } );
    }

    public void setupLibraryBreadCrumbsForAsset( final Project project,
                                                 final Path path ) {
        final String assetName = resourceUtils.getBaseFileName( path );

        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.ASSET_PERSPECTIVE );
        breadcrumbs.addBreadCrumb( LibraryPlaces.ASSET_PERSPECTIVE, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_SCREEN ) );
        breadcrumbs.addBreadCrumb( LibraryPlaces.ASSET_PERSPECTIVE,
                                   project.getProjectName(),
                                   new DefaultPlaceRequest( LibraryPlaces.PROJECT_SCREEN ),
                                   () -> {
                                       projectDetailEvent.fire( new ProjectDetailEvent( project ) );
                                   } );
        breadcrumbs.addBreadCrumb( LibraryPlaces.ASSET_PERSPECTIVE,
                                   assetName,
                                   new DefaultPlaceRequest( LibraryPlaces.ASSET_PERSPECTIVE ),
                                   () -> {
                                       assetDetailEvent.fire( new AssetDetailEvent( project, path ) );
                                   } );
    }
}
