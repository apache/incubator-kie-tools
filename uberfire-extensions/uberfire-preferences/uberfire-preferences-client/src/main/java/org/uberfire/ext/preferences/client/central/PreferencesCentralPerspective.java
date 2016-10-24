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

package org.uberfire.ext.preferences.client.central;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreen;
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
@WorkbenchPerspective(identifier = PreferencesCentralPerspective.IDENTIFIER)
public class PreferencesCentralPerspective {

    public static final String IDENTIFIER = "PreferencesCentralPerspective";

    @Inject
    private UberfireDocks uberfireDocks;

    private UberfireDock dock;

    private PerspectiveDefinition perspective;

    public void perspectiveChangeEvent( @Observes UberfireDockReadyEvent dockReadyEvent ) {
        if ( dockReadyEvent.getCurrentPerspective().equals( IDENTIFIER ) ) {
            uberfireDocks.expand( dock );
        }
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        if ( perspective == null ) {
            return createPerspectiveDefinition();
        }

        return perspective;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        perspective = createPerspectiveDefinition();
        configurePerspective( placeRequest );
    }

    PerspectiveDefinition createPerspectiveDefinition() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Preferences" );

        return perspective;
    }

    void configurePerspective( final PlaceRequest placeRequest ) {
        final PanelDefinition actionsBar = new PanelDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        actionsBar.setHeight( 80 );
        actionsBar.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( PreferencesCentralActionsScreen.IDENTIFIER, placeRequest.getParameters() ) ) );

        perspective.getRoot().insertChild( CompassPosition.SOUTH,
                                           actionsBar );

        setupNavBarDock( new DefaultPlaceRequest( PreferencesCentralNavBarScreen.IDENTIFIER, placeRequest.getParameters() ) );
    }

    private void setupNavBarDock( final PlaceRequest placeRequest ) {
        final String title = placeRequest.getParameter( "title", null );

        if ( dock != null ) {
            uberfireDocks.remove( dock );
        }

        dock = new UberfireDock( UberfireDockPosition.WEST,
                                 "ADJUST",
                                 placeRequest,
                                 IDENTIFIER )
                .withSize( 420 )
                .withLabel( title );

        uberfireDocks.add( dock );
    }
}