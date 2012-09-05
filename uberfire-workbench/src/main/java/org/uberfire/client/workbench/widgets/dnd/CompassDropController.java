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
import org.uberfire.client.workbench.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.WorkbenchPartPresenter;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartDroppedEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;

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

    private WorkbenchPanelPresenter.View              dropTarget;

    @Inject
    private PanelManager                     panelManager;

    @Inject
    private WorkbenchDragAndDropManager      dndManager;

    @Inject
    private Event<WorkbenchPartDroppedEvent> workbenchPartDroppedEvent;

    public void setup(final WorkbenchPanelPresenter.View view) {
        dropTarget = view;
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

        //Move Part from source to target 
        final WorkbenchPartPresenter.View view = (WorkbenchPartPresenter.View) context.draggable;
        final WorkbenchDragContext workbenchContext = dndManager.getWorkbenchContext();
        final PartDefinition sourcePart = workbenchContext.getSourcePart();
        final PanelDefinition sourcePanel = workbenchContext.getSourcePanel();
        final String title = workbenchContext.getTitle();
        final PanelDefinition dropPanel = dropTarget.getPresenter().getDefinition();

        //If the Target Panel is the same as the Source we're trying to reposition the 
        //Source's tab within itself. If the Source Panel has only one Tab there is no 
        //net effect. If we're trying to drop as a new tab there is no net effect.
        if ( sourcePanel.equals( dropPanel ) ) {
            if ( sourcePanel.getParts().size() == 1 ) {
                return;
            }
            if ( p == Position.SELF ) {
                return;
            }
        }

        workbenchPartDroppedEvent.fire( new WorkbenchPartDroppedEvent( sourcePart ) );
        final PanelDefinition targetPanel = panelManager.addWorkbenchPanel( dropPanel,
                                                                            p );
        panelManager.addWorkbenchPart( title,
                                       sourcePart,
                                       targetPanel,
                                       view.getWrappedWidget() );
    }

    @Override
    public Widget getDropTarget() {
        return this.dropTarget.asWidget();
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
    }

}
