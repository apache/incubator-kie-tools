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

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.workbench.widgets.panels.tabpanel.WorkbenchTabPanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class WorkbenchDragAndDropManager {

    private static WorkbenchDragAndDropManager INSTANCE          = new WorkbenchDragAndDropManager();

    //Wrapped DragController
    private PickupDragController               dragController;

    //A registry of SimplePanels and their respective DropController
    private Map<SimplePanel, DropController>   dropControllerMap = new HashMap<SimplePanel, DropController>();

    //The WorkbenchTabPanel a drag operation started on
    private WorkbenchTabPanel                  dragWidgetSource  = null;

    //The Widget being dragged
    private Widget                             dragWidget        = null;

    private WorkbenchDragAndDropManager() {
    }

    public static WorkbenchDragAndDropManager getInstance() {
        return INSTANCE;
    }

    public void init(final AbsolutePanel boundaryPanel) {
        this.dragController = new WorkbenchPickupDragController( boundaryPanel );
        this.dragController.setBehaviorDragProxy( true );
    }

    public void makeDraggable(Widget draggable,
                              Widget dragHandle) {
        assertDragController();
        this.dragController.makeDraggable( draggable,
                                           dragHandle );
    }

    public void registerDropController(final SimplePanel owner,
                                       final DropController dropController) {
        assertDragController();
        dropControllerMap.put( owner,
                               dropController );
        dragController.registerDropController( dropController );
    }

    public void unregisterDropController(final SimplePanel owner) {
        assertDragController();
        final DropController dropController = dropControllerMap.remove( owner );
        dragController.unregisterDropController( dropController );
    }

    public void dragStart(final WorkbenchTabPanel wtp,
                          final Widget w) {
        this.dragWidgetSource = wtp;
        this.dragWidget = w;
    }

    public WorkbenchTabPanel getDragWidgetSource() {
        return this.dragWidgetSource;
    }

    public Widget getDragWidget() {
        return this.dragWidget;
    }

    private void assertDragController() {
        if ( this.dragController == null ) {
            throw new IllegalStateException( "DragAndDropManager has not been initialised. Call init() first." );
        }
    }

}
