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

package org.kie.workbench.common.stunner.project.client.screens;

import com.google.gwt.logging.client.LogConfiguration;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class ProjectDiagramWorkbenchDocks {

    private static Logger LOGGER = Logger.getLogger( ProjectDiagramWorkbenchDocks.class.getName() );
    private final static UberfireDockPosition POSITION = UberfireDockPosition.EAST;

    private final UberfireDocks uberfireDocks;
    String perspectiveId;
    boolean enabled;

    protected ProjectDiagramWorkbenchDocks() {
        this( null );
    }

    @Inject
    public ProjectDiagramWorkbenchDocks( final UberfireDocks uberfireDocks ) {
        this.uberfireDocks = uberfireDocks;
        this.enabled = false;
    }

    public void setup( final String perspectiveId ) {
        this.perspectiveId = perspectiveId;
        setupDocks();
        disableDocks();
    }

    public void enableDocks() {
        if ( !isEnabled() ) {
            log( Level.INFO, "Enabling docks" );
            uberfireDocks.enable( POSITION, perspectiveId );
            this.enabled = true;
        }
    }

    public void disableDocks() {
        if ( isEnabled() ) {
            log( Level.INFO, "Disabling docks" );
            uberfireDocks.disable( POSITION, perspectiveId );
            this.enabled = false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    void onDockReadyEvent( @Observes UberfireDockReadyEvent dockReadyEvent ) {
        final String currentPerspectiveId = dockReadyEvent.getCurrentPerspective();
        log( Level.INFO, "Stunner WB Docks ready for perspective [" + currentPerspectiveId + "]." );
        if ( null != currentPerspectiveId && currentPerspectiveId.equals( this.perspectiveId ) ) {
            enableDocks();
        } else {
            disableDocks();
        }
    }

    private void setupDocks() {
        final UberfireDock propertiesDock = createPropertiesDock( perspectiveId );
        final UberfireDock explorerDock = createExplorerDock( perspectiveId );
        uberfireDocks.add( propertiesDock );
        uberfireDocks.add( explorerDock );
    }

    private UberfireDock createPropertiesDock( final String perspectiveId ) {
        return new UberfireDock( POSITION, "PENCIL_SQUARE_O",
                new DefaultPlaceRequest( ProjectDiagramPropertiesScreen.SCREEN_ID ), perspectiveId )
                .withSize( 450 )
                .withLabel( "Properties" );
    }

    private UberfireDock createExplorerDock( final String perspectiveId ) {
        return new UberfireDock( POSITION, "LIST_UL",
                new DefaultPlaceRequest( ProjectDiagramExplorerScreen.SCREEN_ID ), perspectiveId )
                .withSize( 450 )
                .withLabel( "Explorer" );
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
