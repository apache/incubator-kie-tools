/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.perspectives;

import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.client.widgets.loading.LoadingBox;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.standalone.client.screens.*;
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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@WorkbenchPerspective( identifier = AuthoringPerspective.PERSPECTIVE_ID, isTransient = false )
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
        final Map<String, String> sourceParams = placeRequest.getParameters();
        if ( isOpenCanvas( sourceParams ) ) {
            final Map<String, String> params = new HashMap<String, String>();
            cloneParams( sourceParams, params, "name" );
            cloneParams( sourceParams, params, "defSetId" );
            cloneParams( sourceParams, params, "shapeSetId" );
            cloneParams( sourceParams, params, "title" );
            new Timer() {

                @Override
                public void run() {
                    PlaceRequest diagramScreenPlaceRequest = new DefaultPlaceRequest( DiagramScreen.SCREEN_ID, params );
                    placeManager.goTo( diagramScreenPlaceRequest );
                }

            }.schedule( 500 );

        }
        // }
    }

    private void cloneParams( final Map<String, String> source,
                              final Map<String, String> target,
                              final String key ) {
        if ( null != source.get( key ) ) {
            target.put( key, source.get( key ) );

        }

    }

    private boolean isOpenCanvas( final Map<String, String> params ) {
        if ( null != params && !params.isEmpty() ) {
            return params.containsKey( "name" ) || params.containsKey( "defSetId" );

        }
        return false;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Authoring" );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( HomeAuthoringScreen.SCREEN_ID ) ) );
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
        propertiesPanel.appendChild( CompassPosition.SOUTH, treeExplorerPanel );
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
