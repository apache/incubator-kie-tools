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
package org.uberfire.client.workbench.pmgr.nswe;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.AbstractPanelManagerImpl;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

@ApplicationScoped
public class NSWEPanelManager extends AbstractPanelManagerImpl {

    @Inject
    private NSWEExtendedBeanFactory factory;

    @Inject
    private HeaderPanel headerFooterContainerPanel;

    @Inject
    private WorkbenchPickupDragController dragController;

    public NSWEPanelManager() {
        super( new SimpleLayoutPanel(), new FlowPanel(), new FlowPanel() );
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

        WorkbenchPanelPresenter targetPanelPresenter = getWorkbenchPanelPresenter( targetPanel );
        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }

        switch ( (CompassPosition) position ) {
            case ROOT:
                newPanel = rootPanelDef;
                break;

            case SELF:
                newPanel = targetPanelPresenter.getDefinition();
                break;

            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:

                if ( !childPanel.isMinimized() ) {
                    final WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
                    mapPanelDefinitionToPresenter.put( childPanel,
                                                       childPanelPresenter );

                    targetPanelPresenter.addPanel( childPanel,
                                                   childPanelPresenter.getPanelView(),
                                                   position );
                }
                newPanel = childPanel;
                break;

            default:
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

        onPanelFocus( newPanel );
        return newPanel;
    }

    @Override
    public void setWorkbenchSize( int width,
                                  int height ) {
        headerFooterContainerPanel.setPixelSize( width, height );
        perspectiveRootContainer.setPixelSize( width, height - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight() );
    }

    @Override
    protected void arrangePanelsInDOM() {
        RootLayoutPanel rootPanel = RootLayoutPanel.get();
        rootPanel.add( headerFooterContainerPanel );
        headerFooterContainerPanel.setHeaderWidget( headerPanel );
        headerFooterContainerPanel.setFooterWidget( footerPanel );
        headerFooterContainerPanel.setContentWidget( dragController.getBoundaryPanel() );

        dragController.getBoundaryPanel().add( perspectiveRootContainer );
        setToFillParent( dragController.getBoundaryPanel().getElement().getStyle() );

        Style contentPanelStyle = perspectiveRootContainer.getElement().getStyle();
        setToFillParent( contentPanelStyle );
    }

    private void setToFillParent( Style style ) {
        style.setPosition( com.google.gwt.dom.client.Style.Position.ABSOLUTE );
        style.setTop( 0, Unit.PX );
        style.setBottom( 0, Unit.PX );
        style.setLeft( 0, Unit.PX );
        style.setRight( 0, Unit.PX );
    }

}
