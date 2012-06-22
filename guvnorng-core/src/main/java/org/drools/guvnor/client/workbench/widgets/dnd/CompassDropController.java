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

import javax.inject.Inject;

import org.drools.guvnor.client.workbench.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;
import org.drools.guvnor.client.workbench.widgets.panels.WorkbenchTabLayoutPanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A DragController covering the entire WorkbenchPanel that renders a Compass
 * with which to select the target position of the drag operation.
 */
public class CompassDropController extends SimpleDropController {

    private final CompassWidget compass = CompassWidget.getInstance();

    @Inject
    private PanelManager                panelManager;

    public CompassDropController(final WorkbenchPanel wbp) {
        super( wbp.getParent() );
    }

    @Override
    //When entering a WorkbenchPanel show the Compass
    public void onEnter(DragContext context) {
        compass.onEnter( context );
        super.onEnter( context );
    }

    @Override
    //Hide the WorkbenchPanel's Compass
    public void onLeave(DragContext context) {
        compass.onLeave( context );
        super.onLeave( context );
    }

    @Override
    public void onMove(DragContext context) {
        compass.onMove( context );
        super.onMove( context );
    }

    @Override
    public void onDrop(DragContext context) {

        //If not dropTarget has been identified do nothing
        Position p = compass.getDropPosition();
        if ( p == Position.NONE ) {
            return;
        }

        compass.onDrop( context );

        final WorkbenchPart part = (WorkbenchPart) context.draggable;
        final WorkbenchPanel panel = (WorkbenchPanel) (((SimplePanel) getDropTarget()).getWidget());
        final WorkbenchDragContext workbenchContext = WorkbenchDragAndDropManager.getInstance().getWorkbenchContext();
        final WorkbenchTabLayoutPanel wtp = workbenchContext.getOrigin();

        //If the Target Panel is the same as the Source we're trying to reposition the 
        //Source's tab within itself. If the Source Panel has only one Tab there is no 
        //net effect. If we're trying to drop as a new tab there is no net effect.
        if ( wtp.getParent() == panel ) {
            if ( wtp.getWidgetCount() == 1 ) {
                return;
            }
            if ( p == Position.SELF ) {
                return;
            }
        }

        CloseEvent.fire( part,
                         part );
        panelManager.addWorkbenchPanel( part,
                                        panel,
                                        p );
    }

}
