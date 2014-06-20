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

    private PerspectiveDefinition perspectiveDef;

    public AbstractWorkbenchPerspectiveActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
    }

    @Override
    public void onOpen() {
        super.onOpen();
        if ( perspectiveDef != null ) {
            throw new IllegalStateException( "This perspective activity is already open" );
        }

        //Load the persisted state of the Workbench or use the default Perspective definition if no saved state found
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

    @Override
    public void onClose() {
        if ( !perspectiveDef.isTransient() ) {
            wbServices.save( perspectiveDef );
        }

        perspectiveDef = null;
        super.onClose();
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

    //Initialise Workbench state to that of the provided perspective
    private void initialisePerspective( final PerspectiveDefinition perspective ) {
        this.perspectiveDef = perspective;

        panelManager.setPerspective( perspective );
        setupPanelRecursively( panelManager.getRoot() );
    }

    private void setupPanelRecursively( final PanelDefinition panel ) {
        for ( PartDefinition part : panel.getParts() ) {
            final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
            part.setPlace( place );
            placeManager.goTo( part, panel );
        }

        for ( PanelDefinition child : panel.getChildren() ) {
            final PanelDefinition target = panelManager.addWorkbenchPanel( panel,
                                                                           child,
                                                                           child.getPosition() );
            setupPanelRecursively( target );
        }
    }

    // TODO (UF-88) when PlaceRequest is an immutable value type, cloning will no longer be a sensible operation
    private PlaceRequest clonePlaceAndMergeParameters( final PlaceRequest _place ) {
        return _place.clone();
    }
}