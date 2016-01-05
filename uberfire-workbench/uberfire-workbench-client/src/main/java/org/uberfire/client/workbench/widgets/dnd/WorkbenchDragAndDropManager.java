/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.workbench.widgets.dnd;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A Manager of drag and drop operations within the Workbench.
 */
@ApplicationScoped
public class WorkbenchDragAndDropManager {

    /** Maps panels to the drop controllers we have registered them with (so we can unregister them later). */
    Map<WorkbenchPanelView, DropController> dropControllerMap = new HashMap<WorkbenchPanelView, DropController>();

    //The context of the drag operation
    private WorkbenchDragContext workbenchContext = null;

    @Inject
    WorkbenchPickupDragController dragController;

    @Inject
    BeanFactory factory;

    public void makeDraggable( IsWidget draggable,
                               IsWidget dragHandle ) {
        this.dragController.makeDraggable( draggable.asWidget(),
                                           dragHandle.asWidget() );
    }

    public void registerDropController( final WorkbenchPanelView owner,
                                        final DropController dropController ) {
        dropControllerMap.put( owner,
                               dropController );
        dragController.registerDropController( dropController );
    }

    public void unregisterDropController( final WorkbenchPanelView view ) {
        final DropController dropController = dropControllerMap.remove( view );
        dragController.unregisterDropController( dropController );
        factory.destroy( dropController );
    }

    public void unregisterDropControllers() {
        for ( Map.Entry<WorkbenchPanelView, DropController> e : this.dropControllerMap.entrySet() ) {
            final DropController dropController = dropControllerMap.get( e.getKey() );
            dragController.unregisterDropController( dropController );
            factory.destroy( dropController );
        }
        this.dropControllerMap.clear();
    }

    public void setWorkbenchContext( final WorkbenchDragContext workbenchContext ) {
        this.workbenchContext = workbenchContext;
    }

    public WorkbenchDragContext getWorkbenchContext() {
        return this.workbenchContext;
    }

}
