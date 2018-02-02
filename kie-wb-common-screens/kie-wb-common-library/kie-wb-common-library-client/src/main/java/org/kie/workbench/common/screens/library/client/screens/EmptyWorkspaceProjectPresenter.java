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

import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceUtils;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.isPackageHandler;
import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.isProjectHandler;
import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.isUploadHandler;

public class EmptyWorkspaceProjectPresenter {

    public interface View extends IsElement {

        void init(EmptyWorkspaceProjectPresenter emptyWorkspaceProjectPresenter);

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

    private BusyIndicatorView busyIndicatorView;

    @Inject
    public EmptyWorkspaceProjectPresenter(final View view,
                                          final ResourceUtils resourceUtils,
                                          final NewResourcePresenter newResourcePresenter,
                                          final PlaceManager placeManager,
                                          final LibraryPlaces libraryPlaces,
                                          final ProjectsDetailScreen projectsDetailScreen,
                                          final BusyIndicatorView busyIndicatorView) {
        this.view = view;
        this.resourceUtils = resourceUtils;
        this.newResourcePresenter = newResourcePresenter;
        this.placeManager = placeManager;
        this.libraryPlaces = libraryPlaces;
        this.projectsDetailScreen = projectsDetailScreen;
        this.busyIndicatorView = busyIndicatorView;
    }

    public void show(final WorkspaceProject project) {

        PortablePreconditions.checkNotNull("EmptyProjectPresenter.project",
                                           project);

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
        view.setProjectName(project.getName());
        view.setProjectDetails(projectsDetailScreen.getView());
        placeManager.closePlace(LibraryPlaces.LIBRARY_SCREEN);
        busyIndicatorView.hideBusyIndicator();
    }

    public void goToSettings() {
        libraryPlaces.goToSettings();
    }

    public NewResourceHandler getUploadHandler() {
        return uploadHandler;
    }

    public NewResourcePresenter getNewResourcePresenter() {
        return newResourcePresenter;
    }

    public IsElement getView() {
        return view;
    }
}
