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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.DropPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Drop Controller covering the entire DecoratedWorkbenchPanel that renders a Compass
 * with which to select the target position of the drag operation.
 */
@Dependent
public class CompassDropController implements DropController {

    @Inject
    CompassWidget compass;

    WorkbenchPanelView<?> dropTarget;

    @Inject
    PanelManager panelManager;

    @Inject
    WorkbenchDragAndDropManager dndManager;

    @Inject
    private Event<DropPlaceEvent> workbenchPartDroppedEvent;

    public void setup( final WorkbenchPanelView<?> view ) {
        dropTarget = view;
    }

    @Override
    //When entering a DecoratedWorkbenchPanel show the Compass
    public void onEnter( DragContext context ) {
        compass.onEnter( context );
    }

    @Override
    //Hide the DecoratedWorkbenchPanel's Compass
    public void onLeave( DragContext context ) {
        compass.onLeave( context );
    }

    @Override
    public void onMove( DragContext context ) {
        compass.onMove( context );
    }

    @Override
    public void onDrop( DragContext context ) {

        //If not dropTarget has been identified do nothing
        Position p = compass.getDropPosition();
        if ( p == CompassPosition.NONE ) {
            return;
        }

        compass.onDrop( context );

        //Move Part from source to target
        final WorkbenchDragContext workbenchContext = dndManager.getWorkbenchContext();
        final Menus menus = workbenchContext.getMenus();


        final PanelDefinition sourcePanel = workbenchContext.getSourcePanel();
        final PanelDefinition dropPanel = dropTarget.getPresenter().getDefinition();

        //If the Target Panel is the same as the Source we're trying to reposition the
        //Source's tab within itself. If the Source Panel has only one Tab there is no
        //net effect. If we're trying to drop as a new tab there is no net effect.
        if ( sourcePanel.equals( dropPanel ) ) {
            if ( sourcePanel.getParts().size() == 1 ) {
                return;
            }
            if ( p == CompassPosition.SELF ) {
                return;
            }
        }

        final PlaceRequest place = workbenchContext.getPlace();

        firePartDroppedEvent( place );


        final Integer height = null;
        final Integer width = null;
        final Integer minHeight = workbenchContext.getMinHeight();
        final Integer minWidth = workbenchContext.getMinWidth();

        final PanelDefinition targetPanel = panelManager.addWorkbenchPanel( dropPanel,
                p,
                height,
                width,
                minHeight,
                minWidth );
        final UIPart uiPart = workbenchContext.getUiPart();
        final PartDefinition sourcePart = workbenchContext.getSourcePart();

        panelManager.addWorkbenchPart( place,
                sourcePart,
                targetPanel,
                menus,
                uiPart,
                workbenchContext.getContextId(),
                null,
                null );
    }

    void firePartDroppedEvent( PlaceRequest place ) {
        workbenchPartDroppedEvent.fire( new DropPlaceEvent( place ) );
    }

    @Override
    public Widget getDropTarget() {
        return this.dropTarget.getPartDropRegion();
    }

    @Override
    public void onPreviewDrop( DragContext context ) throws VetoDragException {
    }

}
