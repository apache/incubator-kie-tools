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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.library.AddProjectButtonPresenter;
import org.uberfire.client.mvp.UberElement;

public class EmptyLibraryScreen {

    public interface View extends UberElement<EmptyLibraryScreen> {

        void addAction(HTMLElement action);
    }

    private View view;

    private AddProjectButtonPresenter addProjectButtonPresenter;

    private LibraryPermissions libraryPermissions;

    private LibraryPlaces libraryPlaces;

    @Inject
    public EmptyLibraryScreen(final View view,
                              final AddProjectButtonPresenter addProjectButtonPresenter,
                              final LibraryPermissions libraryPermissions,
                              final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.addProjectButtonPresenter = addProjectButtonPresenter;
        this.libraryPermissions = libraryPermissions;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void setup() {
        view.init(this);

        if (userCanCreateProjects()) {
            view.addAction(addProjectButtonPresenter.getView().getElement());
        }
    }

    public void trySamples() {
        if (userCanCreateProjects()) {
            libraryPlaces.goToTrySamples();
        }
    }

    public void importProject() {
        if (userCanCreateProjects()) {
            libraryPlaces.goToImportRepositoryPopUp();
        }
    }

    boolean userCanCreateProjects() {
        return libraryPermissions.userCanCreateProject(libraryPlaces.getActiveSpace());
    }

    public View getView() {
        return view;
    }
}
