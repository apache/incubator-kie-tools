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

import java.util.List;

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
        panelManager.getRoot().getChildren( Position.NORTH ).clear();
        panelManager.getRoot().getChildren( Position.SOUTH ).clear();
        panelManager.getRoot().getChildren( Position.EAST ).clear();
        panelManager.getRoot().getChildren( Position.WEST ).clear();
        panelManager.getRoot().getChildren( Position.NORTH ).addAll( root.getChildren( Position.NORTH ) );
        panelManager.getRoot().getChildren( Position.SOUTH ).addAll( root.getChildren( Position.SOUTH ) );
        panelManager.getRoot().getChildren( Position.EAST ).addAll( root.getChildren( Position.EAST ) );
        panelManager.getRoot().getChildren( Position.WEST ).addAll( root.getChildren( Position.WEST ) );

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
        final List<PanelDefinition> children = panel.getChildren( position );
        if ( children.size() > 0 ) {
            for ( PanelDefinition child : children ) {
                final PanelDefinition target = panelManager.addWorkbenchPanel( panel,
                                                                               child,
                                                                               position );
                addChildren( target );
            }
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
