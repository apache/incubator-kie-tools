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

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.LibraryBreadCrumbToolbarPresenter;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class LibraryBreadcrumbs {

    private UberfireBreadcrumbs breadcrumbs;


    private TranslationService ts;

    @Inject
    public LibraryBreadcrumbs( UberfireBreadcrumbs breadcrumbs,
                               TranslationService ts ) {
        this.breadcrumbs = breadcrumbs;
        this.ts = ts;
    }

    public LibraryBreadcrumbs() {

    }


    public void setupToolBar( LibraryBreadCrumbToolbarPresenter breadCrumbToolbarPresenter ) {
        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.LIBRARY_PERSPECTIVE );
        breadcrumbs
                .addBreadCrumb( LibraryPlaces.LIBRARY_PERSPECTIVE, ts.getTranslation( LibraryConstants.All_Projects ),
                                new DefaultPlaceRequest( LibraryPlaces.LIBRARY_SCREEN ) );
        breadcrumbs.addToolbar( LibraryPlaces.LIBRARY_PERSPECTIVE, breadCrumbToolbarPresenter.getView().getElement() );
    }

    public void setupAuthoringBreadCrumbsForProject( String projectName ) {
        breadcrumbs.clearBreadCrumbsAndToolBars( PerspectiveIds.AUTHORING );
        breadcrumbs.addBreadCrumb( PerspectiveIds.AUTHORING, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_PERSPECTIVE ) );
        breadcrumbs
                .addBreadCrumb( PerspectiveIds.AUTHORING, projectName,
                                new DefaultPlaceRequest( PerspectiveIds.AUTHORING ) );

    }


    public void setupAuthoringBreadcrumbsForExample() {
        breadcrumbs.clearBreadCrumbsAndToolBars( PerspectiveIds.AUTHORING );
        breadcrumbs.addBreadCrumb( PerspectiveIds.AUTHORING, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_PERSPECTIVE ) );

    }

    public void setupLibraryBreadCrumbs() {
        breadcrumbs.clearBreadCrumbsAndToolBars( LibraryPlaces.LIBRARY_PERSPECTIVE );
        breadcrumbs.addBreadCrumb( LibraryPlaces.LIBRARY_PERSPECTIVE, ts.getTranslation( LibraryConstants.All_Projects ),
                                   new DefaultPlaceRequest( LibraryPlaces.LIBRARY_PERSPECTIVE ) );

    }
}
