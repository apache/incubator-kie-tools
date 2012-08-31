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

import java.util.TreeMap;

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
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Drop Controller for the boundaries (North, South, East or West) of a panel.
 */
@Dependent
public class BoundaryDropController
    implements
    DropController {

    private static Element                   dropTargetHighlight;

    private static final int                 DROP_MARGIN                 = 64;

    private Position                         dropTargetHighlightPosition = Position.NONE;

    private WorkbenchPanel                   dropTarget;

    @Inject
    private PanelManager                     panelManager;

    @Inject
    private WorkbenchDragAndDropManager      dndManager;

    @Inject
    private Event<WorkbenchPartDroppedEvent> workbenchPartDroppedEvent;

    public void setup(final WorkbenchPanel wbp) {
        this.dropTarget = wbp;
        if ( dropTargetHighlight == null ) {
            dropTargetHighlight = Document.get().createDivElement();
            dropTargetHighlight.getStyle().setPosition( Style.Position.ABSOLUTE );
            dropTargetHighlight.getStyle().setVisibility( Visibility.HIDDEN );
            dropTargetHighlight.setClassName( "dropTarget-highlight" );
            dropTargetHighlight.getStyle().setMargin( 0,
                                                      Unit.PX );
            dropTargetHighlight.getStyle().setPadding( 0,
                                                       Unit.PX );
            dropTargetHighlight.getStyle().setBorderWidth( 0,
                                                           Unit.PX );
            Document.get().getBody().appendChild( dropTargetHighlight );
        }
    }

    @Override
    public void onDrop(DragContext context) {

        //If not dropTarget has been identified do nothing
        if ( dropTargetHighlightPosition == Position.NONE ) {
            return;
        }

        //TODO {manstis}
        //final WorkbenchPart part = (WorkbenchPart) context.draggable;
        //final WorkbenchPanel panel = (WorkbenchPanel) (((SimpleLayoutPanel) getDropTarget()).getWidget());
        //final WorkbenchDragContext workbenchContext = dndManager.getWorkbenchContext();
        //final WorkbenchTabLayoutPanel wtp = workbenchContext.getOrigin();

        //If the Target Panel is the same as the Source we're trying to reposition the 
        //Source's tab within itself. If the Source Panel has only one Tab there is no 
        //net effect. If we're trying to drop as a new tab there is no net effect.
        //if ( wtp.getParent() == panel ) {
        //    if ( wtp.getWidgetCount() == 1 ) {
        //        return;
        //    }
        //    if ( dropTargetHighlightPosition == Position.SELF ) {
        //        return;
        //    }
        //}

        //workbenchPartDroppedEvent.fire( new WorkbenchPartDroppedEvent( part ) );
        //final WorkbenchPanel targetPanel = panelManager.addWorkbenchPanel( dropTarget,
        //                                                                   dropTargetHighlightPosition );
        //panelManager.addWorkbenchPart( part,
        //                                targetPanel );
    }

    @Override
    public void onEnter(DragContext context) {
        showDropTarget( context );
    }

    @Override
    public void onLeave(DragContext context) {
        hideDropTarget();
    }

    @Override
    public void onMove(DragContext context) {
        showDropTarget( context );
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
    }

    @Override
    public Widget getDropTarget() {
        //TODO {manstis}
        //return this.dropTarget;
        return  null;
    }

    private void showDropTarget(final DragContext context) {
        Position p = getPosition( context );
        if ( dropTargetHighlightPosition == p ) {
            return;
        }
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        final Widget dropTargetParent = getDropTarget();
        dropTargetHighlightPosition = p;
        switch ( p ) {
            case SELF :
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = dropTargetParent.getOffsetWidth();
                height = dropTargetParent.getOffsetHeight();
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case NORTH :
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = dropTargetParent.getOffsetWidth();
                height = (int) (dropTargetParent.getOffsetHeight() * 0.50);
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case SOUTH :
                x = dropTargetParent.getAbsoluteLeft();
                height = (int) (dropTargetParent.getOffsetHeight() * 0.50);
                y = dropTargetParent.getOffsetHeight() + dropTargetParent.getAbsoluteTop() - height;
                width = dropTargetParent.getOffsetWidth();
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case EAST :
                width = (int) (dropTargetParent.getOffsetWidth() * 0.50);
                x = dropTargetParent.getOffsetWidth() + dropTargetParent.getAbsoluteLeft() - width;
                y = dropTargetParent.getAbsoluteTop();
                height = dropTargetParent.getOffsetHeight();
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case WEST :
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = (int) (dropTargetParent.getOffsetWidth() * 0.50);
                height = dropTargetParent.getOffsetHeight();
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            default :
                hideDropTarget();
        }
    }

    private void showDropTarget(int x,
                                int y,
                                int width,
                                int height) {
        dropTargetHighlight.getStyle().setLeft( x,
                                                Unit.PX );
        dropTargetHighlight.getStyle().setWidth( width,
                                                 Unit.PX );
        dropTargetHighlight.getStyle().setTop( y,
                                               Unit.PX );
        dropTargetHighlight.getStyle().setHeight( height,
                                                  Unit.PX );
        dropTargetHighlight.getStyle().setVisibility( Visibility.VISIBLE );
        dropTargetHighlight.getStyle().setDisplay( Display.BLOCK );
    }

    private void hideDropTarget() {
        dropTargetHighlight.getStyle().setVisibility( Visibility.HIDDEN );
        dropTargetHighlight.getStyle().setDisplay( Display.NONE );
        dropTargetHighlightPosition = Position.NONE;
    }

    private Position getPosition(final DragContext context) {
        int mx = context.mouseX;
        int my = context.mouseY;
        final Widget dropTargetParent = getDropTarget();
        int cxmin = dropTargetParent.getElement().getAbsoluteLeft();
        int cymin = dropTargetParent.getElement().getAbsoluteTop();
        int cxmax = dropTargetParent.getElement().getAbsoluteRight();
        int cymax = dropTargetParent.getElement().getAbsoluteBottom();

        //Possible drop positions. TreeMap is sorted by it's Key hence the first
        //entry will be the position to which the mouse pointer is closest
        TreeMap<Integer, Position> possiblePositions = new TreeMap<Integer, Position>();

        //Default is NONE. Since it has the maximum Integer value 
        //it will always be last in the list of possibilities
        possiblePositions.put( Integer.MAX_VALUE,
                               Position.NONE );

        //Centre position (self)
        CoordinateArea ca = new CoordinateArea( cxmin,
                                                cymin,
                                                cxmax,
                                                cymax );
        if ( mx > ca.getCenter().getLeft() - DROP_MARGIN && mx < ca.getCenter().getLeft() + DROP_MARGIN ) {
            if ( my > ca.getCenter().getTop() - DROP_MARGIN && my < ca.getCenter().getTop() + DROP_MARGIN ) {
                return Position.SELF;
            }
        }

        //Determine which other positions are candidates
        int northDelta = my - cymin;
        int southDelta = cymax - my;
        int eastDelta = cxmax - mx;
        int westDelta = mx - cxmin;

        if ( northDelta <= DROP_MARGIN ) {
            possiblePositions.put( northDelta,
                                   Position.NORTH );
        }
        if ( southDelta <= DROP_MARGIN ) {
            possiblePositions.put( southDelta,
                                   Position.SOUTH );
        }
        if ( eastDelta <= DROP_MARGIN ) {
            possiblePositions.put( eastDelta,
                                   Position.EAST );
        }
        if ( westDelta <= DROP_MARGIN ) {
            possiblePositions.put( westDelta,
                                   Position.WEST );
        }

        //Return the first Value, i.e. lowest value. GWT doesn't support TreeMap.getFirstEntry()
        Integer lowestKey = possiblePositions.firstKey();
        return possiblePositions.get( lowestKey );
    }

}
