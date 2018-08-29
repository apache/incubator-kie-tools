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

package org.kie.workbench.common.screens.library.client.screens.importrepository;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import static org.kie.workbench.common.screens.library.client.screens.importrepository.Source.Kind.EXTERNAL;

@WorkbenchScreen(identifier = LibraryPlaces.IMPORT_PROJECTS_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class ImportProjectsScreen {

    private ImportPresenter presenter;

    @Inject
    public ImportProjectsScreen(final @Source(EXTERNAL) ImportPresenter presenter) {
        this.presenter = presenter;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        presenter.onStartup(placeRequest);
    }

    public void setupEvent(@Observes @Source(EXTERNAL) ImportProjectsSetupEvent event) {
        presenter.setupEvent(event);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return this.presenter.getView().getTitle();
    }

    @WorkbenchPartView
    public ImportPresenter.View getView() {
        return presenter.getView();
    }
}
