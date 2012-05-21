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
import org.drools.guvnor.client.workbench.widgets.panels.tabpanel.WorkbenchTabPanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
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
    }

    @Override
    public void dragStart() {
        final Widget w = super.context.selectedWidgets.get( 0 );
        final WorkbenchTabPanel wtp = (WorkbenchTabPanel) w.getParent().getParent().getParent();
        WorkbenchDragAndDropManager.getInstance().dragStart( wtp,
                                                             w );
        super.dragStart();
    }

    @Override
    protected Widget newDragProxy(DragContext context) {
        AbsolutePanel container = new AbsolutePanel();
        container.getElement().getStyle().setBackgroundColor( "red" );
        container.getElement().getStyle().setProperty( "overflow",
                                                       "visible" );

        int offsetX = context.mouseX - context.draggable.getAbsoluteLeft() - (dragProxy.getWidth() / 2);
        int offsetY = context.mouseY - context.draggable.getAbsoluteTop() - (dragProxy.getHeight() / 2);
        container.add( dragProxy,
                       offsetX,
                       offsetY );
        return container;
    }

}
