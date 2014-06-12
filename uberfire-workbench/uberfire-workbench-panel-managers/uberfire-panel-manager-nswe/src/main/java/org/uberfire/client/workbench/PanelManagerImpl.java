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
package org.uberfire.client.workbench;

import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class PanelManagerImpl extends AbstractPanelManagerImpl {

    @Inject
    private NSWEExtendedBeanFactory factory;


    public PanelManagerImpl() {
    }

    //constructor for unit testing
    public PanelManagerImpl( final NSWEExtendedBeanFactory factory,
                             final Event<PlaceGainFocusEvent> placeGainFocusEvent,
                             final Event<PlaceLostFocusEvent> placeLostFocusEvent,
                             final Event<SelectPlaceEvent> selectPlaceEvent,
                             final WorkbenchStatusBarPresenter statusBar ) {
        this.factory = factory;
        this.placeGainFocusEvent = placeGainFocusEvent;
        this.placeLostFocusEvent = placeLostFocusEvent;
        this.selectPlaceEvent = selectPlaceEvent;
        this.statusBar = statusBar;
    }

    @Override
    protected BeanFactory getBeanFactory(){
        return factory;
    };

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final PanelDefinition childPanel,
                                              final Position position ) {

        PanelDefinition newPanel = null;

        // TODO (hbraun): is the target panel always the perspective?
        WorkbenchPanelPresenter targetPanelPresenter = getWorkbenchPanelPresenter( targetPanel );

        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel, targetPanelPresenter );
        }

        switch ( position ) {
            case ROOT:
                newPanel = root;
                break;

            case SELF:
                newPanel = targetPanelPresenter.getDefinition();
                break;

            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case CENTER:

                if ( !childPanel.isMinimized() ) {

                    WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
                    // TODO (hbraun): Why is cached? doesn't seem to be reused ...
                    mapPanelDefinitionToPresenter.put( childPanel, childPanelPresenter );

                    // TODO (hbraun): why no remove callback before the addPanel invocation?

                    targetPanelPresenter.addPanel( childPanel,
                            childPanelPresenter.getPanelView(),
                            position );
                }
                newPanel = childPanel;
                break;

            default:
                throw new IllegalArgumentException( "Unhandled Position '"+position+"': Expect subsequent errors." );
        }

        onPanelFocus( newPanel );
        return newPanel;
    }

}
