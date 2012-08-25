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

import org.uberfire.client.workbench.WorkbenchPanel;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;
import org.uberfire.client.workbench.perspectives.PerspectivePartDefinition;
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
    public void launch(WorkbenchPanel rootPanel) {
        final PerspectiveDefinition perspective = getPerspective();
        buildPerspective( rootPanel,
                          perspective.getParts() );
    }

    private void buildPerspective(final WorkbenchPanel target,
                                  final Set<PerspectivePartDefinition> parts) {
        for ( PerspectivePartDefinition part : parts ) {
            final WorkbenchPanel targetPanel = panelManager.addWorkbenchPanel( target,
                                                                               part.getPosition() );
            placeManager.goTo( part.getPlace(),
                               targetPanel );
            switch ( part.getPosition() ) {
                case NORTH :
                case SOUTH :
                case EAST :
                case WEST :
                    buildPerspective( targetPanel,
                                      part.getParts() );
                    break;
            }

        }
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
