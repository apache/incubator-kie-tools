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

import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.Position;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A pop-up "compass" widget that appears centralised on it's parent Drop Target
 * and permits drop events on the compass points.
 */
public class CompassWidget extends PopupPanel
    implements
    DropController {

    private static CompassWidget instance;

    private static Element       dropTargetHighlight;

    private final Image          northWidget        = new Image( WorkbenchResources.INSTANCE.images().compassNorth() );
    private final Image          southWidget        = new Image( WorkbenchResources.INSTANCE.images().compassSouth() );
    private final Image          eastWidget         = new Image( WorkbenchResources.INSTANCE.images().compassEast() );
    private final Image          westWidget         = new Image( WorkbenchResources.INSTANCE.images().compassWest() );
    private final Image          centreWidget       = new Image( WorkbenchResources.INSTANCE.images().compassCentre() );

    private final FlexTable      container          = new FlexTable();

    private Position             dropTargetPosition = Position.NONE;

    public static CompassWidget getInstance() {
        if ( instance == null ) {
            instance = new CompassWidget();
        }
        return instance;
    }

    private CompassWidget() {
        super();
        this.setStyleName( "dropTarget-compass" );
        this.container.setCellPadding( 0 );
        this.container.setCellSpacing( 0 );

        //Setup drop indicator
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

        //Setup Compass images
        northWidget.getElement().getStyle().setOpacity( 0.75 );
        southWidget.getElement().getStyle().setOpacity( 0.75 );
        eastWidget.getElement().getStyle().setOpacity( 0.75 );
        westWidget.getElement().getStyle().setOpacity( 0.75 );
        container.setWidget( 0,
                             1,
                             northWidget );
        container.setWidget( 1,
                             0,
                             westWidget );
        container.setWidget( 1,
                             1,
                             centreWidget );
        container.setWidget( 1,
                             2,
                             eastWidget );
        container.setWidget( 2,
                             1,
                             southWidget );

        setWidget( container );
    }

    @Override
    public void onEnter(DragContext context) {
        show( context );
    }

    @Override
    public void onLeave(DragContext context) {
        hide();
    }

    @Override
    public void onMove(DragContext context) {
        final Location l = new CoordinateLocation( context.mouseX,
                                                   context.mouseY );
        final WidgetArea northWidgetArea = new WidgetArea( northWidget,
                                                           null );
        final WidgetArea southWidgetArea = new WidgetArea( southWidget,
                                                           null );
        final WidgetArea eastWidgetArea = new WidgetArea( eastWidget,
                                                           null );
        final WidgetArea westWidgetArea = new WidgetArea( westWidget,
                                                           null );
        final WidgetArea centreWidgetArea = new WidgetArea( centreWidget,
                                                            null );
        Position p = Position.NONE;
        if ( northWidgetArea.intersects( l ) ) {
            p = Position.NORTH;
        } else if ( southWidgetArea.intersects( l ) ) {
            p = Position.SOUTH;
        } else if ( eastWidgetArea.intersects( l ) ) {
            p = Position.EAST;
        } else if ( westWidgetArea.intersects( l ) ) {
            p = Position.WEST;
        } else if ( centreWidgetArea.intersects( l ) ) {
            p = Position.SELF;
        }
        if ( p != dropTargetPosition ) {
            dropTargetPosition = p;
            showDropTarget( context,
                            p );
        }
    }

    public Position getDropPosition() {
        return this.dropTargetPosition;
    }

    @Override
    public Widget getDropTarget() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onDrop(DragContext context) {
        this.dropTargetPosition = Position.NONE;
        hideDropTarget();
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        throw new UnsupportedOperationException();
    }

    private void show(final DragContext context) {

        //Get centre of DropTarget
        final Widget dropTargetParent = context.dropController.getDropTarget();
        int cxmin = dropTargetParent.getElement().getAbsoluteLeft();
        int cymin = dropTargetParent.getElement().getAbsoluteTop();
        int cxmax = dropTargetParent.getElement().getAbsoluteRight();
        int cymax = dropTargetParent.getElement().getAbsoluteBottom();

        final CoordinateArea ca = new CoordinateArea( cxmin,
                                                      cymin,
                                                      cxmax,
                                                      cymax );

        //Display Compass if not already visible
        if ( !isAttached() ) {
            setPopupPositionAndShow( new PositionCallback() {

                @Override
                public void setPosition(int offsetWidth,
                                        int offsetHeight) {
                    setPopupPosition( ca.getCenter().getLeft() - (offsetWidth / 2),
                                                ca.getCenter().getTop() - (offsetHeight / 2) );
                }

            } );

        } else {
            setPopupPosition( ca.getCenter().getLeft() - (getOffsetWidth() / 2),
                                        ca.getCenter().getTop() - (getOffsetHeight() / 2) );
        }

    }

    private void showDropTarget(final DragContext context,
                                final Position p) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        final Widget dropTargetParent = context.dropController.getDropTarget();
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
        dropTargetPosition = Position.NONE;
    }

}
