/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.cms.perspective;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.cms.screen.explorer.PerspectivesExplorerScreen;
import org.dashbuilder.client.cms.screen.explorer.NavigationExplorerScreen;
import org.dashbuilder.client.cms.screen.home.ContentManagerHomeScreen;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.ext.layout.editor.client.LayoutComponentPaletteScreen;
import org.uberfire.ext.plugin.client.perspective.editor.PerspectiveEditorPresenter;
import org.uberfire.ext.plugin.client.perspective.editor.events.PerspectiveEditorFocusEvent;
import org.uberfire.lifecycle.*;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = ContentManagerPerspective.PERSPECTIVE_ID)
public class ContentManagerPerspective {

    public static final String PERSPECTIVE_ID = "ContentManagerPerspective";

    @Inject
    ContentManagerI18n i18n;

    @Inject
    UberfireDocks uberfireDocks;

    UberfireDock perspectivesExplorerDock;
    UberfireDock navigationExplorerDock;
    UberfireDock componentPaletteDock;
    boolean componentDockVisible = true;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return buildPerspective();
    }

    private PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName(ContentManagerConstants.INSTANCE.contentManagerHome());
        perspective.getRoot().addPart(ContentManagerHomeScreen.SCREEN_ID);
        return perspective;
    }

    @PostConstruct
    public void init() {
        perspectivesExplorerDock = new UberfireDock(UberfireDockPosition.WEST,
                IconType.FILE_TEXT_O.toString(),
                new DefaultPlaceRequest(PerspectivesExplorerScreen.SCREEN_ID), PERSPECTIVE_ID).withSize(330)
                .withLabel(i18n.capitalizeFirst(i18n.getPerspectivesResourceName()));

        navigationExplorerDock = new UberfireDock(UberfireDockPosition.WEST,
                IconType.NAVICON.toString(),
                new DefaultPlaceRequest(NavigationExplorerScreen.SCREEN_ID), PERSPECTIVE_ID).withSize(330)
                .withLabel(ContentManagerConstants.INSTANCE.contentExplorerNavigation());

        componentPaletteDock = new UberfireDock(UberfireDockPosition.WEST,
                IconType.CUBES.toString(),
                new DefaultPlaceRequest(LayoutComponentPaletteScreen.SCREEN_ID), PERSPECTIVE_ID).withSize(330)
                .withLabel(ContentManagerConstants.INSTANCE.componentPalette());

        uberfireDocks.add(perspectivesExplorerDock);
        uberfireDocks.add(navigationExplorerDock);
        uberfireDocks.add(componentPaletteDock);
    }

    @OnOpen
    public void onOpen() {
        refreshDocks(false, perspectivesExplorerDock);
    }

    private void refreshDocks(boolean showComponentPalette, UberfireDock dockToOpen) {

        if (showComponentPalette && !componentDockVisible) {
            uberfireDocks.add(componentPaletteDock);
            componentDockVisible = true;
        }
        if (!showComponentPalette && componentDockVisible) {
            uberfireDocks.remove(componentPaletteDock);
            componentDockVisible = false;
        }

        uberfireDocks.show(UberfireDockPosition.WEST, PERSPECTIVE_ID);

        if (dockToOpen != null) {
            uberfireDocks.open(dockToOpen);
        }
    }

    public void onPerspectiveEditorFocus(@Observes PerspectiveEditorFocusEvent event) {
        refreshDocks(true, componentPaletteDock);
    }

    public void oonPerspectiveEditorHidden(@Observes PlaceHiddenEvent event) {
        String placeId = event.getPlace().getIdentifier();
        if (PerspectiveEditorPresenter.ID.equals(placeId)) {
            refreshDocks(false, null);
        }
    }
}
