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
import org.drools.guvnor.client.workbench.widgets.panels.WorkbenchTabLayoutPanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class WorkbenchPickupDragController extends PickupDragController {

    private final Image dragProxy = new Image( GuvnorResources.INSTANCE.guvnorImages().workbenchPanelDragProxy() );

    public WorkbenchPickupDragController(final AbsolutePanel boundaryPanel) {
        super( boundaryPanel,
               false );
        setBehaviorDragStartSensitivity( 1 );
    }

    @Override
    public void dragStart() {
        final WorkbenchPart part = (WorkbenchPart) super.context.selectedWidgets.get( 0 );
        final WorkbenchTabLayoutPanel wtp = (WorkbenchTabLayoutPanel) part.getParent().getParent().getParent();
        final WorkbenchDragContext context = new WorkbenchDragContext( part,
                                                                       wtp );
        WorkbenchDragAndDropManager.getInstance().setWorkbenchContext( context );
        super.dragStart();
        final Widget movablePanel = getMoveablePanel();
        if ( movablePanel != null ) {
            DOMUtil.fastSetElementPosition( movablePanel.getElement(),
                                            super.context.mouseX,
                                            super.context.mouseY );
        }

    }

    @Override
    public void dragMove() {
        super.dragMove();
        final Widget movablePanel = getMoveablePanel();
        if ( movablePanel != null ) {
            DOMUtil.fastSetElementPosition( movablePanel.getElement(),
                                            super.context.mouseX,
                                            super.context.mouseY );
        }
    }

    @Override
    protected Widget newDragProxy(DragContext context) {
        AbsolutePanel container = new AbsolutePanel();
        container.getElement().getStyle().setProperty( "overflow",
                                                       "visible" );

        //Offset to centre of dragProxy
        int offsetX = 0 - ((int) (dragProxy.getWidth() * 0.5));
        int offsetY = 0 - ((int) (dragProxy.getWidth() * 1.5));
        container.add( dragProxy,
                       offsetX,
                       offsetY );
        return container;
    }

    private Widget getMoveablePanel() {
        for ( int index = 0; index < context.boundaryPanel.getWidgetCount(); index++ ) {
            final Widget w = context.boundaryPanel.getWidget( index );
            if ( w.getStyleName().equals( DragClientBundle.INSTANCE.css().movablePanel() ) ) {
                return w;
            }
        }
        return null;
    }

}
