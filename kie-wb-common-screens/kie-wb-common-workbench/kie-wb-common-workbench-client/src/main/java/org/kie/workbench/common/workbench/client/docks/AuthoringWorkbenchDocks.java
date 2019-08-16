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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.docks.WorkbenchDocksHandler;
import org.kie.workbench.common.workbench.client.events.LayoutEditorFocusEvent;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.ext.layout.editor.client.LayoutComponentPaletteScreen;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class AuthoringWorkbenchDocks
        implements AuthoringEditorDock {

    protected DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    protected UberfireDocks uberfireDocks;

    protected String authoringPerspectiveIdentifier = null;

    protected UberfireDock projectExplorerDock;

    protected UberfireDock componentPaletteDock;

    protected String currentPerspectiveIdentifier = null;

    protected boolean projectExplorerEnabled = true;

    protected boolean componentPaletteEnabled = false;

    protected ManagedInstance<WorkbenchDocksHandler> installedHandlers;

    protected WorkbenchDocksHandler activeHandler = null;

    protected UberfireDock[] activeDocks;

    @Inject
    public AuthoringWorkbenchDocks(final UberfireDocks uberfireDocks,
                                   final ManagedInstance<WorkbenchDocksHandler> installedHandlers) {
        this.uberfireDocks = uberfireDocks;
        this.installedHandlers = installedHandlers;
    }

    @PostConstruct
    public void initialize() {

        // Initializing the handlers
        installedHandlers.iterator().forEachRemaining(handler ->
                                                              handler.init(() -> setActiveHandler(handler)));
    }

    @Override
    public boolean isSetup() {
        return authoringPerspectiveIdentifier != null;
    }

    public void perspectiveChangeEvent(@Observes UberfireDockReadyEvent dockReadyEvent) {
        currentPerspectiveIdentifier = dockReadyEvent.getCurrentPerspective();
        if (authoringPerspectiveIdentifier != null && dockReadyEvent.getCurrentPerspective().equals(authoringPerspectiveIdentifier)) {
            if (projectExplorerEnabled) {
                expandProjectExplorer();
            }
        }
    }

    @Override
    public void setup(String authoringPerspectiveIdentifier,
                      PlaceRequest projectExplorerPlaceRequest) {
        this.authoringPerspectiveIdentifier = authoringPerspectiveIdentifier;
        projectExplorerDock = new UberfireDock(UberfireDockPosition.WEST,
                                               IconType.FOLDER_OPEN.toString(),
                                               projectExplorerPlaceRequest,
                                               authoringPerspectiveIdentifier).withSize(400).withLabel(constants.DocksProjectExplorerTitle());

        componentPaletteDock = new UberfireDock(UberfireDockPosition.WEST,
                                                IconType.CUBES.toString(),
                                                new DefaultPlaceRequest(LayoutComponentPaletteScreen.SCREEN_ID),
                                                authoringPerspectiveIdentifier).withSize(400).withLabel(constants.LayoutEditorComponentPalette());

        uberfireDocks.add(projectExplorerDock);
        uberfireDocks.hide(UberfireDockPosition.EAST,
                           authoringPerspectiveIdentifier);
    }

    public void setActiveHandler(WorkbenchDocksHandler handler) {

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

    @Override
    public void hide() {

        if (componentPaletteEnabled) {
            uberfireDocks.remove(componentPaletteDock);
            componentPaletteEnabled = false;
        }
        uberfireDocks.hide(UberfireDockPosition.WEST,
                           authoringPerspectiveIdentifier);
        projectExplorerEnabled = false;
    }

    @Override
    public void show() {
        uberfireDocks.show(UberfireDockPosition.WEST,
                           authoringPerspectiveIdentifier);
        projectExplorerEnabled = true;
    }

    public void expandProjectExplorer() {
        if (projectExplorerDock != null && !componentPaletteEnabled) {
            uberfireDocks.open(projectExplorerDock);
        }
    }

    @Override
    public void expandAuthoringDock(final UberfireDock dockToOpen) {
        uberfireDocks.show(UberfireDockPosition.EAST, authoringPerspectiveIdentifier);

        if (dockToOpen != null) {
            uberfireDocks.open(dockToOpen);
        }
    }

    private void refreshWestDocks(boolean showComponentPalette, UberfireDock dockToOpen) {

        if (showComponentPalette && !componentPaletteEnabled) {
            uberfireDocks.add(componentPaletteDock);
            componentPaletteEnabled = true;
        }
        if (!showComponentPalette && componentPaletteEnabled) {
            uberfireDocks.remove(componentPaletteDock);
            componentPaletteEnabled = false;
        }

        uberfireDocks.show(UberfireDockPosition.WEST, authoringPerspectiveIdentifier);

        if (dockToOpen != null) {
            uberfireDocks.open(dockToOpen);
        }
    }

    public void onLayoutEditorFocus(@Observes LayoutEditorFocusEvent event) {
        refreshWestDocks(true, componentPaletteDock);
    }

    public void onLayoutEditorClose(@Observes PlaceHiddenEvent event) {
        String placeId = event.getPlace().getIdentifier();
        if ("FormEditor".equals(placeId)) {
            refreshWestDocks(false, null);
        }
    }

    @PreDestroy
    public void clear() {
        activeDocks = null;
        activeHandler = null;
        installedHandlers.destroyAll();
    }
}
