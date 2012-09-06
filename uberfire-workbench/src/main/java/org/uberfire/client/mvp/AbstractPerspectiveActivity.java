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

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.widgets.panels.PanelManager;

/**
 * Base class for Perspective Activities
 */
public abstract class AbstractPerspectiveActivity
    implements
    PerspectiveActivity {

    @Inject
    private PanelManager panelManager;

    @Inject
    PlaceManager         placeManager;

    @Override
    public void launch() {
        final PerspectiveDefinition perspective = getPerspective();
        final PanelDefinition root = perspective.getRoot();

        panelManager.getRoot().getParts().clear();
        panelManager.getRoot().getParts().addAll( root.getParts() );
        panelManager.getRoot().setChild( Position.NORTH,
                                         root.getChild( Position.NORTH ) );
        panelManager.getRoot().setChild( Position.SOUTH,
                                         root.getChild( Position.SOUTH ) );
        panelManager.getRoot().setChild( Position.EAST,
                                         root.getChild( Position.EAST ) );
        panelManager.getRoot().setChild( Position.WEST,
                                         root.getChild( Position.WEST ) );

        for ( PartDefinition part : panelManager.getRoot().getParts() ) {
            placeManager.goTo( part,
                               panelManager.getRoot() );
        }
        buildPerspective( panelManager.getRoot(),
                          Position.NORTH );
        buildPerspective( panelManager.getRoot(),
                          Position.SOUTH );
        buildPerspective( panelManager.getRoot(),
                          Position.EAST );
        buildPerspective( panelManager.getRoot(),
                          Position.WEST );
    }

    private void buildPerspective(final PanelDefinition panel,
                                  final Position position) {
        final PanelDefinition child = panel.getChild( position );
        if ( child != null ) {
            final PanelDefinition target = panelManager.addWorkbenchPanel( panel,
                                                                           child,
                                                                           position );
            addChildren( target );
        }
    }

    private void addChildren(final PanelDefinition panel) {
        for ( PartDefinition part : panel.getParts() ) {
            placeManager.goTo( part,
                               panel );
        }
        buildPerspective( panel,
                          Position.NORTH );
        buildPerspective( panel,
                          Position.SOUTH );
        buildPerspective( panel,
                          Position.EAST );
        buildPerspective( panel,
                          Position.WEST );
    }

    @Override
    public void onReveal() {
        //Do nothing.   
    }

    @Override
    public String[] getRoles() {
        return null;
    }

    @Override
    public String[] getTraitTypes() {
        return null;
    }

    public abstract PerspectiveDefinition getPerspective();

    public abstract String getIdentifier();

}
