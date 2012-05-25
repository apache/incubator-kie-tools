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
package org.drools.guvnor.client.workbench.widgets.dnd;

import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.panels.tabpanel.WorkbenchTabPanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class WorkbenchPickupDragController extends PickupDragController {

    private final Image dragProxy = new Image( GuvnorResources.INSTANCE.workbenchPanelDragProxy() );

    public WorkbenchPickupDragController(final AbsolutePanel boundaryPanel) {
        super( boundaryPanel,
               false );
        setBehaviorDragStartSensitivity( 1 );
    }

    @Override
    public void dragStart() {
        super.dragStart();
        final WorkbenchPart part = (WorkbenchPart) super.context.selectedWidgets.get( 0 );
        final WorkbenchTabPanel wtp = (WorkbenchTabPanel) part.getParent().getParent().getParent();
        final WorkbenchDragContext wbc = new WorkbenchDragContext( part,
                                                                   wtp );
        WorkbenchDragAndDropManager.getInstance().setWorkbenchContext( wbc );

        Widget moveablePanel = getMovablePanel();
        if ( moveablePanel != null ) {
            DOM.setStyleAttribute( moveablePanel.getElement(),
                                   "left",
                                   context.mouseX + "px" );
            DOM.setStyleAttribute( moveablePanel.getElement(),
                                   "top",
                                   context.mouseY + "px" );
        }
    }

    @Override
    public void dragMove() {
        super.dragMove();
        Widget moveablePanel = getMovablePanel();
        if ( moveablePanel != null ) {
            DOM.setStyleAttribute( moveablePanel.getElement(),
                                   "left",
                                   context.mouseX + "px" );
            DOM.setStyleAttribute( moveablePanel.getElement(),
                                   "top",
                                   context.mouseY + "px" );
        }
    }

    //Hack to find PickupDragController's movablePanel. PickupDragController positions the movablePanel where 
    //the draggable is located. This does not work for hidden Widgets (i.e. a DeckPanel's hidden content). 
    //See https://groups.google.com/d/topic/gwt-dnd/7KcAL3sHCFc/discussion
    private Widget getMovablePanel() {
        for ( int index = 0; index < context.boundaryPanel.getWidgetCount(); index++ ) {
            final Widget w = context.boundaryPanel.getWidget( index );
            if ( w.getStyleName().equals( DragClientBundle.INSTANCE.css().movablePanel() ) ) {
                return w;
            }
        }
        return null;
    }

    @Override
    protected Widget newDragProxy(DragContext context) {
        AbsolutePanel container = new AbsolutePanel();
        container.getElement().getStyle().setProperty( "overflow",
                                                       "visible" );

        // context.draggable is the Widget, not the DragHandle so offset
        int offsetX = 0 - ((int) (dragProxy.getWidth() * 0.5));
        int offsetY = 0 - ((int) (dragProxy.getHeight() * 1.5));
        container.add( dragProxy,
                       offsetX,
                       offsetY );
        return container;
    }

}
