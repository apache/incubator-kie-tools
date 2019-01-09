/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.perspective;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = LibraryPlaces.LIBRARY_PERSPECTIVE)
public class LibraryPerspective {

    private LibraryPlaces libraryPlaces;

    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    private PerspectiveDefinition perspectiveDefinition;
    private boolean refresh = true;

    public LibraryPerspective() {
    }

    @Inject
    public LibraryPerspective(final LibraryPlaces libraryPlaces,
                              final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent) {
        this.libraryPlaces = libraryPlaces;
        this.projectContextChangeEvent = projectContextChangeEvent;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        if (perspectiveDefinition == null) {
            perspectiveDefinition = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
            perspectiveDefinition.setName("Library Perspective");
        }

        return perspectiveDefinition;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.refresh = Boolean.parseBoolean(placeRequest.getParameter("refresh", "true"));
        this.libraryPlaces.init(this);
    }

    public void perspectiveChangeEvent(@Observes PerspectiveChange event) {
        if (event.getIdentifier().equals(LibraryPlaces.LIBRARY_PERSPECTIVE)) {
            if (refresh) {
                libraryPlaces.refresh(() -> {
                    if (getRootPanel() != null) {
                        libraryPlaces.goToLibrary();
                    }
                });
            } else {
                libraryPlaces.refresh(() -> {
                });
            }
        }
    }

    @OnClose
    public void onClose() {
        libraryPlaces.hideDocks();
        projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent());
    }

    public PanelDefinition getRootPanel() {
        return buildPerspective().getRoot();
    }
}
