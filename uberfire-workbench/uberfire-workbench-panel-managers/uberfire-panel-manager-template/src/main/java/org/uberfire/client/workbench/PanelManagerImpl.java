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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
public class PanelManagerImpl extends AbstractPanelManagerImpl {

    @Inject
    private DefaultBeanFactory factory;

    public PanelManagerImpl() {
    }

    //constructor for unit testing
    public PanelManagerImpl( final DefaultBeanFactory factory,
                             final Event<BeforeClosePlaceEvent> beforeClosePlaceEvent,
                             final Event<PlaceGainFocusEvent> placeGainFocusEvent,
                             final Event<PlaceLostFocusEvent> placeLostFocusEvent,
                             final Event<SelectPlaceEvent> selectPlaceEvent,
                             final WorkbenchStatusBarPresenter statusBar ) {
        this.factory = factory;
        this.beforeClosePlaceEvent = beforeClosePlaceEvent;
        this.placeGainFocusEvent = placeGainFocusEvent;
        this.placeLostFocusEvent = placeLostFocusEvent;
        this.selectPlaceEvent = selectPlaceEvent;
        this.statusBar = statusBar;
    }

    @Override
    protected BeanFactory getBeanFactory() {
        return factory;
    }

    @Override
    public void setPerspective( final PerspectiveDefinition perspective ) {
        if ( isATemplatePerspective( perspective ) ) {
            super.setPerspective( perspective );
        } else {
            throw new NotATemplatePerspectiveException();
        }
    }

    private boolean isATemplatePerspective( PerspectiveDefinition perspective ) {
        return perspective instanceof TemplatePerspectiveDefinitionImpl;
    }

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final PanelDefinition childPanel,
                                              final Position position ) {

        WorkbenchPanelPresenter targetPanelPresenter = createTargetPanelPresenter( targetPanel );

        final WorkbenchPanelPresenter childPanelPresenter = createChildPresenter( childPanel );

        targetPanelPresenter.addPanel( childPanel,
                                       childPanelPresenter.getPanelView(),
                                       position );
        onPanelFocus( childPanel );

        return childPanel;
    }

    private WorkbenchPanelPresenter createChildPresenter( PanelDefinition childPanel ) {
        final WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
        mapPanelDefinitionToPresenter.put( childPanel,
                                           childPanelPresenter );
        return childPanelPresenter;
    }

    private WorkbenchPanelPresenter createTargetPanelPresenter( PanelDefinition targetPanel ) {
        WorkbenchPanelPresenter targetPanelPresenter = mapPanelDefinitionToPresenter.get( targetPanel );
        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }
        return targetPanelPresenter;
    }

    public class NotATemplatePerspectiveException extends RuntimeException {

    }
}
