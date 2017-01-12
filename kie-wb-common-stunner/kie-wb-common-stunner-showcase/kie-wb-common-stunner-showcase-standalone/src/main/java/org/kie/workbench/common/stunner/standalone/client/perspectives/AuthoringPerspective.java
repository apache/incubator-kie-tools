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

import org.kie.workbench.common.stunner.client.widgets.loading.LoadingBox;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.standalone.client.screens.BS3PaletteScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.FormsPropertiesScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.NavigatorScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.NotificationsScreen;
import org.kie.workbench.common.stunner.standalone.client.screens.TreeExplorerScreen;
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
@WorkbenchPerspective( identifier = AuthoringPerspective.PERSPECTIVE_ID, isTransient = false, isDefault = true )
public class AuthoringPerspective {

    public static final String PERSPECTIVE_ID = "AuthoringPerspective";
    public static final int WEST_PANEL_WIDTH = BS3PaletteWidgetImpl.getDefaultWidth();
    public static final int EAST_PANEL_WIDTH = 450;

    PanelDefinition palettePanel;
    PanelDefinition notificationsPanel;
    PanelDefinition propertiesPanel;
    PanelDefinition treeExplorerPanel;

    @Inject
    LoadingBox loadingBox;

    @Inject
    PlaceManager placeManager;

    private PlaceRequest placeRequest;

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        // if ( null == this.placeRequest ) {
        this.placeRequest = placeRequest;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Authoring" );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( NavigatorScreen.SCREEN_ID ) ) );
        palettePanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        palettePanel.setMinWidth( WEST_PANEL_WIDTH );
        palettePanel.setWidth( WEST_PANEL_WIDTH );
        palettePanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( BS3PaletteScreen.SCREEN_ID ) ) );
        treeExplorerPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        treeExplorerPanel.setMinWidth( EAST_PANEL_WIDTH );
        treeExplorerPanel.setWidth( EAST_PANEL_WIDTH );
        treeExplorerPanel.setMinHeight( 100 );
        treeExplorerPanel.setHeight( 300 );
        treeExplorerPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( TreeExplorerScreen.SCREEN_ID ) ) );
        propertiesPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        propertiesPanel.setMinWidth( EAST_PANEL_WIDTH );
        propertiesPanel.setWidth( EAST_PANEL_WIDTH );
        propertiesPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( FormsPropertiesScreen.SCREEN_ID ) ) );
        propertiesPanel.appendChild( CompassPosition.SOUTH,
                                     treeExplorerPanel );
        notificationsPanel = new PanelDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        notificationsPanel.setMinWidth( 400 );
        notificationsPanel.setWidth( 400 );
        notificationsPanel.setMinHeight( 100 );
        notificationsPanel.setHeight( 100 );
        notificationsPanel.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( NotificationsScreen.SCREEN_ID ) ) );
        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           palettePanel );
        perspective.getRoot().insertChild( CompassPosition.EAST,
                                           propertiesPanel );
        perspective.getRoot().insertChild( CompassPosition.SOUTH,
                                           notificationsPanel );
        return perspective;
    }
}
