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

import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceUtils;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mvp.PlaceRequest;

import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.isPackageHandler;
import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.isProjectHandler;
import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.isUploadHandler;

@WorkbenchScreen(identifier = LibraryPlaces.EMPTY_PROJECT_SCREEN)
public class EmptyProjectScreen {

    public interface View extends UberElement<EmptyProjectScreen> {

        void setProjectName(String projectName);

        void setProjectDetails(IsElement element);

        void addResourceHandler(NewResourceHandler newResourceHandler);
    }

    private View view;

    private ResourceUtils resourceUtils;

    private NewResourcePresenter newResourcePresenter;

    private PlaceManager placeManager;

    private LibraryPlaces libraryPlaces;

    private NewResourceHandler uploadHandler;

    private ProjectsDetailScreen projectsDetailScreen;

    ProjectInfo projectInfo;

    @Inject
    public EmptyProjectScreen(final View view,
                              final ResourceUtils resourceUtils,
                              final NewResourcePresenter newResourcePresenter,
                              final PlaceManager placeManager,
                              final LibraryPlaces libraryPlaces,
                              final ProjectsDetailScreen projectsDetailScreen) {
        this.view = view;
        this.resourceUtils = resourceUtils;
        this.newResourcePresenter = newResourcePresenter;
        this.placeManager = placeManager;
        this.libraryPlaces = libraryPlaces;
        this.projectsDetailScreen = projectsDetailScreen;
    }

    public void onStartup(@Observes final ProjectDetailEvent projectDetailEvent) {
        this.projectInfo = projectDetailEvent.getProjectInfo();

        view.init(this);

        resourceUtils.getOrderedNewResourceHandlers().stream().filter(newResourceHandler -> newResourceHandler.canCreate()).forEach(newResourceHandler -> {
            if (isUploadHandler(newResourceHandler)) {
                uploadHandler = newResourceHandler;
            } else if (!isPackageHandler(newResourceHandler)
                    && !isProjectHandler(newResourceHandler)) {
                view.addResourceHandler(newResourceHandler);
            }
        });

        this.projectsDetailScreen.setMetricsEnabled(false);
        view.setProjectName(projectInfo.getProject().getProjectName());
        view.setProjectDetails(projectsDetailScreen.getView());
        placeManager.closePlace(LibraryPlaces.LIBRARY_SCREEN);
    }

    public void refreshOnFocus(@Observes final PlaceGainFocusEvent placeGainFocusEvent) {
        final PlaceRequest place = placeGainFocusEvent.getPlace();
        if (projectInfo != null && place.getIdentifier().equals(LibraryPlaces.EMPTY_PROJECT_SCREEN)) {
            libraryPlaces.goToProject(projectInfo);
        }
    }

    public void goToSettings() {
        libraryPlaces.goToSettings(projectInfo);
    }

    public NewResourceHandler getUploadHandler() {
        return uploadHandler;
    }

    public NewResourcePresenter getNewResourcePresenter() {
        return newResourcePresenter;
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
