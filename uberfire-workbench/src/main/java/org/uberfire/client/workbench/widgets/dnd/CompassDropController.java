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
package org.uberfire.client.workbench.widgets.dnd;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPanel;
import org.uberfire.client.workbench.WorkbenchPart;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartDroppedEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.client.workbench.widgets.panels.WorkbenchTabLayoutPanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Drop Controller covering the entire WorkbenchPanel that renders a Compass
 * with which to select the target position of the drag operation.
 */
@Dependent
public class CompassDropController
    implements
    DropController {

    private final CompassWidget              compass = CompassWidget.getInstance();

    private WorkbenchPanel                   dropTarget;

    @Inject
    private PanelManager                     panelManager;

    @Inject
    private WorkbenchDragAndDropManager      dndManager;

    @Inject
    private Event<WorkbenchPartDroppedEvent> workbenchPartDroppedEvent;

    public void setup(final WorkbenchPanel wbp) {
        dropTarget = wbp;;
    }

    @Override
    //When entering a WorkbenchPanel show the Compass
    public void onEnter(DragContext context) {
        compass.onEnter( context );
    }

    @Override
    //Hide the WorkbenchPanel's Compass
    public void onLeave(DragContext context) {
        compass.onLeave( context );
    }

    @Override
    public void onMove(DragContext context) {
        compass.onMove( context );
    }

    @Override
    public void onDrop(DragContext context) {

        //If not dropTarget has been identified do nothing
        Position p = compass.getDropPosition();
        if ( p == Position.NONE ) {
            return;
        }

        compass.onDrop( context );

        //TODO {manstis}
        //final WorkbenchPart part = (WorkbenchPart) context.draggable;
        //final WorkbenchDragContext workbenchContext = dndManager.getWorkbenchContext();
        //final WorkbenchTabLayoutPanel wtp = workbenchContext.getOrigin();

        //If the Target Panel is the same as the Source we're trying to reposition the 
        //Source's tab within itself. If the Source Panel has only one Tab there is no 
        //net effect. If we're trying to drop as a new tab there is no net effect.
        //if ( wtp.getParent() == dropTarget ) {
        //    if ( wtp.getWidgetCount() == 1 ) {
        //        return;
        //    }
        //    if ( p == Position.SELF ) {
        //        return;
        //    }
        //}

        //workbenchPartDroppedEvent.fire( new WorkbenchPartDroppedEvent( part ) );
        //final WorkbenchPanel targetPanel = panelManager.addWorkbenchPanel( dropTarget,
        //                                                                   p );
        //panelManager.addWorkbenchPart( part,
        //                                targetPanel );
    }

    @Override
    public Widget getDropTarget() {
        //TODO {manstis}
        //return this.dropTarget;
        return null;
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
    }

}
