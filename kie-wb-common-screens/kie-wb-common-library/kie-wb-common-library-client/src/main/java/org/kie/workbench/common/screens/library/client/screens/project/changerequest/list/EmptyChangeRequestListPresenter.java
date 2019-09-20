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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.list;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;

@Dependent
public class EmptyChangeRequestListPresenter {

    private final View view;
    private final ProjectController projectController;
    private final LibraryPlaces libraryPlaces;
    private final Promises promises;
    private WorkspaceProject workspaceProject;

    @Inject
    public EmptyChangeRequestListPresenter(final View view,
                                           final ProjectController projectController,
                                           final LibraryPlaces libraryPlaces,
                                           final Promises promises) {
        this.view = view;
        this.projectController = projectController;
        this.libraryPlaces = libraryPlaces;
        this.promises = promises;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = this.libraryPlaces.getActiveWorkspace();

        this.view.init(this);

        projectController.canSubmitChangeRequest(workspaceProject).then(userCanSubmitChangeRequest -> {
            view.enableSubmitChangeRequestButton(userCanSubmitChangeRequest);

            return promises.resolve();
        });
    }

    public View getView() {
        return view;
    }

    public void goToSubmitChangeRequest() {
        projectController.canSubmitChangeRequest(workspaceProject).then(userCanSubmitChangeRequest -> {
            if (Boolean.TRUE.equals(userCanSubmitChangeRequest)) {
                this.libraryPlaces.goToSubmitChangeRequestScreen();
            }

            return promises.resolve();
        });
    }

    public interface View extends UberElemental<EmptyChangeRequestListPresenter> {

        void enableSubmitChangeRequestButton(final boolean isEnabled);
    }
}