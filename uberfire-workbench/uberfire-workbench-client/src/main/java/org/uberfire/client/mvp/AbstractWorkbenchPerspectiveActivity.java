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

import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

/**
 * Implementation of behaviour common to all perspective activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchPerspective}.
 */
public abstract class AbstractWorkbenchPerspectiveActivity extends AbstractActivity implements PerspectiveActivity {

    @Inject
    private PanelManager panelManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    public AbstractWorkbenchPerspectiveActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        loadState();
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

    //Load the persisted state of the Workbench or use the default Perspective definition if no saved state found
    private void loadState() {

        final PerspectiveDefinition perspective = getPerspective();

        if ( perspective.isTransient() ) {
            //Transient Perspectives are not saved and hence cannot be loaded
            initialisePerspective( perspective );

        } else {

            wbServices.loadPerspective( perspective.getName(), new ParameterizedCommand<PerspectiveDefinition>() {
                @Override
                public void execute( final PerspectiveDefinition response ) {
                    if ( response == null ) {
                        initialisePerspective( perspective );
                    } else {
                        initialisePerspective( response );
                    }
                }
            } );
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
        return _place.clone();
    }
}