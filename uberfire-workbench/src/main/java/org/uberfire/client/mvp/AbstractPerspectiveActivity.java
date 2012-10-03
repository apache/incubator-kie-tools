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

import java.util.Map;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.workbench.WorkbenchServices;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * Base class for Perspective Activities
 */
public abstract class AbstractPerspectiveActivity extends AbstractActivity
    implements
    PerspectiveActivity {

    @Inject
    private PanelManager              panelManager;

    @Inject
    private PlaceManager              placeManager;

    @Inject
    private Caller<WorkbenchServices> wbServices;

    public AbstractPerspectiveActivity(final PlaceManager placeManager) {
        super( placeManager );
    }

    @Override
    public void launch(final PlaceRequest place,
                       final Command callback) {
        super.launch( place,
                      callback );
        saveState();
    }

    //Save the current state of the Workbench
    private void saveState() {

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
                public void callback(Void response) {
                    placeManager.closeAllPlaces();
                    loadState();
                }
            } ).save( perspective );
        }
    }

    //Load the persisted state of the Workbench or use the default Perspective definition if no saved state found
    private void loadState() {
        final PerspectiveDefinition perspective = getPerspective();

        if ( perspective.isTransient() ) {
            //Transient Perspectives are not saved and hence cannot be loaded
            initialisePerspective( perspective );

        } else {

            wbServices.call( new RemoteCallback<PerspectiveDefinition>() {
                @Override
                public void callback(PerspectiveDefinition response) {
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
    private void initialisePerspective(final PerspectiveDefinition perspective) {

        panelManager.setPerspective( perspective );

        for ( PartDefinition part : panelManager.getRoot().getParts() ) {
            //TODO {manstis} Hack for salaboy until Perspectives become class-based
            final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
            part.setPlace( place );
            //--- End of Hack
            placeManager.goTo( part,
                               panelManager.getRoot() );
        }
        buildPerspective( panelManager.getRoot() );
        onReveal();
    }

    private void buildPerspective(final PanelDefinition panel) {
        for ( PanelDefinition child : panel.getChildren() ) {
            if ( child != null ) {
                final PanelDefinition target = panelManager.addWorkbenchPanel( panel,
                                                                               child,
                                                                               child.getPosition() );
                addChildren( target );
            }
        }
    }

    private void addChildren(final PanelDefinition panel) {
        for ( PartDefinition part : panel.getParts() ) {
            //TODO {manstis} Hack for salaboy until Perspectives become class-based
            final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
            part.setPlace( place );
            //--- End of Hack
            placeManager.goTo( part,
                               panel );
        }
        buildPerspective( panel );
    }

    public abstract PerspectiveDefinition getPerspective();

    public abstract String getIdentifier();

    private PlaceRequest clonePlaceAndMergeParameters(final PlaceRequest place) {
        final PlaceRequest clone = new DefaultPlaceRequest( place.getIdentifier() );
        for ( Map.Entry<String, String> parameter : place.getParameters().entrySet() ) {
            clone.addParameter( parameter.getKey(),
                                parameter.getValue() );
        }
        for ( Map.Entry<String, String> parameter : this.place.getParameters().entrySet() ) {
            clone.addParameter( parameter.getKey(),
                                parameter.getValue() );
        }
        return clone;
    }

}
