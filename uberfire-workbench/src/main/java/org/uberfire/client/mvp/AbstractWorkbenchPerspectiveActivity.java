/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.mvp;

import java.util.Set;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.services.WorkbenchServices;

/**
 * Base class for Perspective Activities
 */
public abstract class AbstractWorkbenchPerspectiveActivity extends AbstractActivity
        implements
        PerspectiveActivity {

    @Inject
    private PanelManager panelManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<WorkbenchServices> wbServices;

    public AbstractWorkbenchPerspectiveActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        super.launch( place,
                      callback );
        saveState();
    }

    @Override
    public void onStart() {
        //Do nothing.  
    }

    @Override
    public void onStart( final PlaceRequest place ) {
        //Do nothing.  
    }

    @Override
    public void onClose() {
        //Do nothing.  
    }

    @Override
    public void onReveal() {
    }

    @Override
    public abstract PerspectiveDefinition getPerspective();

    @Override
    public abstract String getIdentifier();

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public Menus getMenus() {
        return null;
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    //Save the current state of the Workbench
    private void saveState() {

        onClose();

        final PerspectiveDefinition perspective = panelManager.getPerspective();

        if ( perspective == null ) {
            //On startup the Workbench has not been set to contain a perspective
            loadState();

        } else if ( perspective.isTransient() ) {
            //Transient Perspectives are not saved
            placeManager.closeAllPlaces();
            loadState();

        } else {
            //Save first, then close all places before loading persisted state
            wbServices.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                    placeManager.closeAllPlaces();
                    loadState();
                }
            } ).save( perspective );
        }
    }

    //Load the persisted state of the Workbench or use the default Perspective definition if no saved state found
    private void loadState() {

        //Call OnStart before getting the Perspective definition in case any setup is required by @WorkbenchPerspective's
        onStart( place );

        final PerspectiveDefinition perspective = getPerspective();

        if ( perspective.isTransient() ) {
            //Transient Perspectives are not saved and hence cannot be loaded
            initialisePerspective( perspective );

        } else {

            wbServices.call( new RemoteCallback<PerspectiveDefinition>() {
                @Override
                public void callback( PerspectiveDefinition response ) {
                    if ( response == null ) {
                        initialisePerspective( perspective );
                    } else {
                        initialisePerspective( response );
                    }
                }
            } ).load( perspective.getName() );
        }
    }

    //Initialise Workbench state to that of the provided perspective
    private void initialisePerspective( final PerspectiveDefinition perspective ) {

        panelManager.setPerspective( perspective );

        Set<PartDefinition> parts = panelManager.getRoot().getParts();
        for ( PartDefinition part : parts ) {
            final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
            part.setPlace( place );
            placeManager.goTo( part, panelManager.getRoot() );
        }
        buildPerspective( panelManager.getRoot() );

        onReveal();
    }

    private void buildPerspective( final PanelDefinition panel ) {
        for ( PanelDefinition child : panel.getChildren() ) {
            final PanelDefinition target = panelManager.addWorkbenchPanel( panel,
                                                                           child,
                                                                           child.getPosition() );
            addChildren( target );
        }
    }

    private void addChildren( final PanelDefinition panel ) {
        Set<PartDefinition> parts = panel.getParts();
        for ( PartDefinition part : parts ) {
            final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
            part.setPlace( place );
            placeManager.goTo( part, panel );
        }
        buildPerspective( panel );
    }

    private PlaceRequest clonePlaceAndMergeParameters( final PlaceRequest _place ) {
        return  _place.clone();
    }
}
