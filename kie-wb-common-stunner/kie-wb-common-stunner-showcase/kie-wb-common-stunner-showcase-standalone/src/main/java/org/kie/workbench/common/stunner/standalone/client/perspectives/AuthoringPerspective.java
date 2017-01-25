/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.client.widgets.views.LoadingBox;
import org.kie.workbench.common.stunner.standalone.client.screens.DiagramsNavigatorScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.NotificationsScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.SessionDiagramPreviewScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.SessionPaletteScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.SessionPropertiesScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.SessionTreeExplorerScreen;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = AuthoringPerspective.PERSPECTIVE_ID, isTransient = false)
public class AuthoringPerspective {

    public static final String PERSPECTIVE_ID = "AuthoringPerspective";
    public static final int WEST_PANEL_WIDTH = BS3PaletteWidgetImpl.getDefaultWidth();
    public static final int EAST_PANEL_WIDTH = 450;
    public static final int EAST_PANEL_HEIGHT = 300;

    PanelDefinition palettePanel;
    PanelDefinition notificationsPanel;
    PanelDefinition propertiesPanel;
    PanelDefinition treeExplorerPanel;
    PanelDefinition previewPanel;

    @Inject
    LoadingBox loadingBox;

    @Inject
    PlaceManager placeManager;

    private PlaceRequest placeRequest;

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        // if ( null == this.placeRequest ) {
        this.placeRequest = placeRequest;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName("Authoring");
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest(DiagramsNavigatorScreen.SCREEN_ID)));
        palettePanel = new PanelDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        palettePanel.setWidth(WEST_PANEL_WIDTH);
        palettePanel.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(SessionPaletteScreen.SCREEN_ID)));
        treeExplorerPanel = new PanelDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        treeExplorerPanel.setWidth(EAST_PANEL_WIDTH);
        treeExplorerPanel.setHeight(EAST_PANEL_HEIGHT);
        treeExplorerPanel.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(SessionTreeExplorerScreen.SCREEN_ID)));
        previewPanel = new PanelDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        previewPanel.setWidth(EAST_PANEL_WIDTH);
        previewPanel.setHeight(EAST_PANEL_HEIGHT);
        previewPanel.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(SessionDiagramPreviewScreen.SCREEN_ID)));
        propertiesPanel = new PanelDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        propertiesPanel.setWidth(EAST_PANEL_WIDTH);
        propertiesPanel.setHeight(EAST_PANEL_HEIGHT);
        propertiesPanel.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(SessionPropertiesScreen.SCREEN_ID)));
        propertiesPanel.appendChild(CompassPosition.NORTH,
                                    previewPanel);
        propertiesPanel.appendChild(CompassPosition.SOUTH,
                                    treeExplorerPanel);
        notificationsPanel = new PanelDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        notificationsPanel.setWidth(400);
        notificationsPanel.setHeight(100);
        notificationsPanel.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(NotificationsScreen.SCREEN_ID)));
        perspective.getRoot().insertChild(CompassPosition.WEST,
                                          palettePanel);
        perspective.getRoot().insertChild(CompassPosition.EAST,
                                          propertiesPanel);
        perspective.getRoot().insertChild(CompassPosition.SOUTH,
                                          notificationsPanel);
        return perspective;
    }
}