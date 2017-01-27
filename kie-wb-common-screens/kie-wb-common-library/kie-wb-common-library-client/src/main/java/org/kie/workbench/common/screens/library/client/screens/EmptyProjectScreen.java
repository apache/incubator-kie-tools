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

package org.kie.workbench.common.screens.library.client.screens;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mvp.PlaceRequest;

import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.*;

@WorkbenchScreen(identifier = LibraryPlaces.EMPTY_PROJECT_SCREEN)
public class EmptyProjectScreen {

    public interface View extends UberElement<EmptyProjectScreen> {

        void setProjectName( String projectName );

        void addResourceHandler( NewResourceHandler newResourceHandler );
    }

    private View view;

    private ManagedInstance<NewResourceHandler> newResourceHandlers;

    private NewResourcePresenter newResourcePresenter;

    private PlaceManager placeManager;

    private LibraryPlaces libraryPlaces;

    private NewResourceHandler uploadHandler;

    ProjectInfo projectInfo;

    @Inject
    public EmptyProjectScreen( final View view,
                               final ManagedInstance<NewResourceHandler> newResourceHandlers,
                               final NewResourcePresenter newResourcePresenter,
                               final PlaceManager placeManager,
                               final LibraryPlaces libraryPlaces ) {
        this.view = view;
        this.newResourceHandlers = newResourceHandlers;
        this.newResourcePresenter = newResourcePresenter;
        this.placeManager = placeManager;
        this.libraryPlaces = libraryPlaces;
    }

    public void onStartup( @Observes final ProjectDetailEvent projectDetailEvent ) {
        this.projectInfo = projectDetailEvent.getProjectInfo();

        for ( NewResourceHandler newResourceHandler : getNewResourceHandlers() ) {
            if ( newResourceHandler.canCreate() ) {
                if ( isUploadHandler( newResourceHandler ) ) {
                    uploadHandler = newResourceHandler;
                } else if ( !isPackageHandler( newResourceHandler )
                        && !isProjectHandler( newResourceHandler ) ) {
                    view.addResourceHandler( newResourceHandler );
                }
            }
        }

        view.setProjectName( projectInfo.getProject().getProjectName() );
        placeManager.closePlace( LibraryPlaces.LIBRARY_SCREEN );
    }

    public void refreshOnFocus( @Observes final PlaceGainFocusEvent placeGainFocusEvent ) {
        final PlaceRequest place = placeGainFocusEvent.getPlace();
        if ( projectInfo != null && place.getIdentifier().equals( LibraryPlaces.EMPTY_PROJECT_SCREEN ) ) {
            libraryPlaces.goToProject( projectInfo );
        }
    }

    public void goToSettings() {
        libraryPlaces.goToSettings( projectInfo );
    }

    public NewResourceHandler getUploadHandler() {
        return uploadHandler;
    }

    public NewResourcePresenter getNewResourcePresenter() {
        return newResourcePresenter;
    }

    Iterable<NewResourceHandler> getNewResourceHandlers() {
        return newResourceHandlers;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Empty Project Screen";
    }

    @WorkbenchPartView
    public UberElement<EmptyProjectScreen> getView() {
        return view;
    }
}
