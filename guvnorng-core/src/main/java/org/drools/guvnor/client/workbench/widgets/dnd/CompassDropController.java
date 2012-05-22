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

import java.util.TreeMap;

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;
import org.drools.guvnor.client.workbench.widgets.panels.tabpanel.WorkbenchTabPanel;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class CompassDropController extends SimpleDropController {

    private static Element   dropTargetHighlight;

    private static final int DROP_MARGIN                 = 64;

    private Position         dropTargetHighlightPosition = Position.NONE;

    public CompassDropController(final WorkbenchPanel wbp) {
        super( wbp );

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

        final WorkbenchPanel target = (WorkbenchPanel) getDropTarget();
        final WorkbenchTabPanel wtp = WorkbenchDragAndDropManager.getInstance().getDragWidgetSource();
        final Widget w = WorkbenchDragAndDropManager.getInstance().getDragWidget();

        //If the Target Panel is the same as the Source we're trying to reposition the 
        //Source's tab within itself. If the Source Panel has only one Tab there is no 
        //net effect. If we're trying to drop as a new tab there is no net effect.
        if ( wtp.getParent() == target ) {
            if ( wtp.getTabBar().getTabCount() == 1 ) {
                return;
            }
            if ( dropTargetHighlightPosition == Position.SELF ) {
                return;
            }
        }

        //DeckPanel clears the height and width of a Widget when it is removed
        //so remember the original sizes before repositioning in the target Panel
        final int oldHeight = w.getOffsetHeight();
        final int oldWidth = w.getOffsetWidth();
        //wtp.remove( w );
        PanelManager.getInstance().addWorkbenchPanel( "TODO",
                                                      target,
                                                      dropTargetHighlightPosition,
                                                      w );

        //DeckPanel sets the height and width of an inserted Widget to 100% if
        //the Widget does not have a height or width. DeckPanel kindly removed
        //the Widget's height and width when removed above so we need to reset.
        w.setPixelSize( oldWidth,
                        oldHeight );
    }

    @Override
    public void onEnter(DragContext context) {
        showDropTarget( context );
        super.onEnter( context );
    }

    @Override
    public void onLeave(DragContext context) {
        hideDropTarget();
        super.onLeave( context );
    }

    @Override
    public void onMove(DragContext context) {
        showDropTarget( context );
        super.onMove( context );
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        super.onPreviewDrop( context );
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
        final Widget dropTargetParent = getDropTarget().getParent();
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
        final Widget dropTargetParent = getDropTarget().getParent();
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
