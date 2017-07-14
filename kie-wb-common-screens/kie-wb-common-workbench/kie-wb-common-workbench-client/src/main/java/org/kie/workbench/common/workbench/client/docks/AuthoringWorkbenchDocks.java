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

package org.kie.workbench.common.workbench.client.docks;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.PlaceRequest;

@ApplicationScoped
public class AuthoringWorkbenchDocks {

    protected DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    protected UberfireDocks uberfireDocks;

    protected String authoringPerspectiveIdentifier;

    protected UberfireDock projectExplorerDock;

    protected LibraryInternalPreferences libraryInternalPreferences;

    protected String currentPerspectiveIdentifier = null;

    protected boolean projectExplorerEnabled = true;

    protected ManagedInstance<WorkbenchDocksHandler> installedHandlers;

    protected WorkbenchDocksHandler activeHandler = null;

    protected UberfireDock[] activeDocks;

    @Inject
    public AuthoringWorkbenchDocks(final UberfireDocks uberfireDocks,
                                   final ManagedInstance<WorkbenchDocksHandler> installedHandlers,
                                   final LibraryInternalPreferences libraryInternalPreferences) {
        this.uberfireDocks = uberfireDocks;
        this.installedHandlers = installedHandlers;
        this.libraryInternalPreferences = libraryInternalPreferences;
    }

    @PostConstruct
    public void initialize() {
        // Initializing the handlers
        installedHandlers.iterator().forEachRemaining(handler ->
                                                              handler.init(() -> setActiveHandler(handler)));
    }

    public void perspectiveChangeEvent(@Observes UberfireDockReadyEvent dockReadyEvent) {
        currentPerspectiveIdentifier = dockReadyEvent.getCurrentPerspective();
        if (authoringPerspectiveIdentifier != null && dockReadyEvent.getCurrentPerspective().equals(authoringPerspectiveIdentifier)) {
            if (projectExplorerEnabled) {
                expandProjectExplorer();
            }
        }
    }

    public void setup(String authoringPerspectiveIdentifier,
                      PlaceRequest projectExplorerPlaceRequest) {
        this.authoringPerspectiveIdentifier = authoringPerspectiveIdentifier;
        projectExplorerDock = new UberfireDock(UberfireDockPosition.WEST,
                                               "ADJUST",
                                               projectExplorerPlaceRequest,
                                               authoringPerspectiveIdentifier).withSize(400).withLabel(constants.DocksProjectExplorerTitle());
        uberfireDocks.add(
                projectExplorerDock
        );
        uberfireDocks.hide(UberfireDockPosition.EAST,
                              authoringPerspectiveIdentifier);
    }

    public void setActiveHandler(WorkbenchDocksHandler handler) {
        if (!isAuthoringActive()) {
            return;
        }

        // If there's an active handler let's check if it should refresh docks
        if (activeHandler != null) {
            if (activeHandler.equals(handler) && !activeHandler.shouldRefreshDocks()) {
                return;
            }
        }

        // setting the new handler as active
        activeHandler = handler;

        if (activeHandler.shouldDisableDocks()) {
            // disable docks
            uberfireDocks.hide(UberfireDockPosition.EAST,
                                  currentPerspectiveIdentifier);
        } else {
            // first remove the existing docks
            if (activeDocks != null) {
                uberfireDocks.remove(activeDocks);
            }

            // getting docks from the handler and  refreshing
            Collection<UberfireDock> docks = activeHandler.provideDocks(currentPerspectiveIdentifier);
            activeDocks = docks.toArray(new UberfireDock[docks.size()]);
            uberfireDocks.add(activeDocks);
            uberfireDocks.show(UberfireDockPosition.EAST,
                                 currentPerspectiveIdentifier);
        }
    }

    public boolean isAuthoringActive() {
        return authoringPerspectiveIdentifier != null &&
                authoringPerspectiveIdentifier.equals(currentPerspectiveIdentifier);
    }

    public void hide() {
        uberfireDocks.hide(UberfireDockPosition.WEST,
                              authoringPerspectiveIdentifier);
        projectExplorerEnabled = false;
    }

    public void show() {
        uberfireDocks.show(UberfireDockPosition.WEST,
                             authoringPerspectiveIdentifier);
        projectExplorerEnabled = true;

        libraryInternalPreferences.load(loadedLibraryInternalPreferences -> {
                                            if (loadedLibraryInternalPreferences.isProjectExplorerExpanded()) {
                                                expandProjectExplorer();
                                            }
                                        },
                                        parameter -> {
                                        });
    }

    public void expandProjectExplorer() {
        if (projectExplorerDock != null) {
            uberfireDocks.open(projectExplorerDock);
        }
    }

    public void projectExplorerExpandedEvent(@Observes final UberfireDocksInteractionEvent uberfireDocksInteractionEvent) {
        final UberfireDock targetDock = uberfireDocksInteractionEvent.getTargetDock();
        if (targetDock == null) {
            return;
        }
        if (targetDock.equals(projectExplorerDock)) {
            final UberfireDocksInteractionEvent.InteractionType interactionType = uberfireDocksInteractionEvent.getType();
            if (interactionType.equals(UberfireDocksInteractionEvent.InteractionType.OPENED)) {
                setProjectExplorerExpandedPreference(true);
            } else if (interactionType.equals(UberfireDocksInteractionEvent.InteractionType.CLOSED)) {
                setProjectExplorerExpandedPreference(false);
            }
        }
    }

    void setProjectExplorerExpandedPreference(final boolean expand) {
        libraryInternalPreferences.load(loadedLibraryInternalPreferences -> {
                                            if (expand != loadedLibraryInternalPreferences.isProjectExplorerExpanded()) {
                                                loadedLibraryInternalPreferences.setProjectExplorerExpanded(expand);
                                                loadedLibraryInternalPreferences.save();
                                            }
                                        },
                                        error -> {
                                        });
    }

    @PreDestroy
    public void clear() {
        activeDocks = null;
        activeHandler = null;
        installedHandlers.destroyAll();
    }
}
