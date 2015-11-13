/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.views.pfly.dnd;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.widgets.dnd.CompassWidget;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

/**
 * A pop-up widget with arrows in the four cardinal directions, each of which is a separate drop target. The center of
 * the widget is a fifth drop target representing the parent widget itself. The compass centers itself over its parent's
 * Drop Target when displayed.
 */
@ApplicationScoped
public class CompassWidgetImpl implements CompassWidget {

    public static final String FA_ACTIVE = "fa-active";

    interface CompassWidgetBinder extends UiBinder<PopupPanel, CompassWidgetImpl> {}

    private static CompassWidgetBinder uiBinder = GWT.create( CompassWidgetBinder.class );

    private Element dropTargetHighlight;

    @UiField
    DockLayoutPanel container;

    @UiField
    PopupPanel popup;

    @UiField
    Widget south;

    @UiField
    Widget north;

    @UiField
    Widget west;

    @UiField
    Widget east;

    @UiField
    Widget centre;

    private CompassPosition dropTargetPosition = CompassPosition.NONE;

    @PostConstruct
    void init() {
        popup = uiBinder.createAndBindUi( this );

        //Setup drop indicator
        if ( dropTargetHighlight == null ) {
            dropTargetHighlight = Document.get().createDivElement();
            dropTargetHighlight.getStyle().setPosition( Style.Position.ABSOLUTE );
            dropTargetHighlight.getStyle().setVisibility( Visibility.HIDDEN );
            dropTargetHighlight.setClassName( WorkbenchResources.INSTANCE.CSS().dropTargetHighlight() );
            Document.get().getBody().appendChild( dropTargetHighlight );
        }

        north.ensureDebugId( "CompassWidget-north" );
        south.ensureDebugId( "CompassWidget-south" );
        east.ensureDebugId( "CompassWidget-east" );
        west.ensureDebugId( "CompassWidget-west" );
        centre.ensureDebugId( "CompassWidget-centre" );
    }

    @Override
    public void onEnter( DragContext context ) {
        show( context );
    }

    @Override
    public void onLeave( DragContext context ) {
        popup.hide();
    }

    @Override
    public void onMove( DragContext context ) {
        final Location l = new CoordinateLocation( context.mouseX,
                                                   context.mouseY );
        final WidgetArea northWidgetArea = new WidgetArea( north, null );
        final WidgetArea southWidgetArea = new WidgetArea( south, null );
        final WidgetArea eastWidgetArea = new WidgetArea( east, null );
        final WidgetArea westWidgetArea = new WidgetArea( west, null );
        final WidgetArea centreWidgetArea = new WidgetArea( centre, null );
        CompassPosition p = CompassPosition.NONE;
        if ( northWidgetArea.intersects( l ) ) {
            p = CompassPosition.NORTH;
        } else if ( southWidgetArea.intersects( l ) ) {
            p = CompassPosition.SOUTH;
        } else if ( eastWidgetArea.intersects( l ) ) {
            p = CompassPosition.EAST;
        } else if ( westWidgetArea.intersects( l ) ) {
            p = CompassPosition.WEST;
        } else if ( centreWidgetArea.intersects( l ) ) {
            p = CompassPosition.SELF;
        }
        if ( p != dropTargetPosition ) {
            dropTargetPosition = p;
            showDropTarget( context, p );
        }
    }

    @Override
    public Position getDropPosition() {
        return this.dropTargetPosition;
    }

    @Override
    public Widget getDropTarget() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onDrop( DragContext context ) {
        this.dropTargetPosition = CompassPosition.NONE;
        highlightActiveDropTarget( null );
        hideDropTarget();
    }

    @Override
    public void onPreviewDrop( DragContext context ) throws VetoDragException {
        throw new UnsupportedOperationException();
    }

    private void show( final DragContext context ) {

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
        if ( !popup.isAttached() ) {
            popup.setPopupPositionAndShow( new PopupPanel.PositionCallback() {

                @Override
                public void setPosition( int offsetWidth,
                                         int offsetHeight ) {
                    popup.setPopupPosition( ca.getCenter().getLeft() - ( offsetWidth / 2 ),
                            ca.getCenter().getTop() - ( offsetHeight / 2 ) );
                }

            } );

        } else {
            popup.setPopupPosition( ca.getCenter().getLeft() - ( popup.getOffsetWidth() / 2 ),
                    ca.getCenter().getTop() - ( popup.getOffsetHeight() / 2 ) );
        }
    }

    private void showDropTarget( final DragContext context,
                                 final CompassPosition p ) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        final Widget dropTargetParent = context.dropController.getDropTarget();
        switch ( p ) {
            case SELF:
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = dropTargetParent.getOffsetWidth();
                height = dropTargetParent.getOffsetHeight();
                highlightActiveDropTarget( centre );
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case NORTH:
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = dropTargetParent.getOffsetWidth();
                height = (int) ( dropTargetParent.getOffsetHeight() * 0.50 );
                highlightActiveDropTarget( north );
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case SOUTH:
                x = dropTargetParent.getAbsoluteLeft();
                height = (int) ( dropTargetParent.getOffsetHeight() * 0.50 );
                y = dropTargetParent.getOffsetHeight() + dropTargetParent.getAbsoluteTop() - height;
                width = dropTargetParent.getOffsetWidth();
                highlightActiveDropTarget( south );
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case EAST:
                width = (int) ( dropTargetParent.getOffsetWidth() * 0.50 );
                x = dropTargetParent.getOffsetWidth() + dropTargetParent.getAbsoluteLeft() - width;
                y = dropTargetParent.getAbsoluteTop();
                height = dropTargetParent.getOffsetHeight();
                highlightActiveDropTarget( east );
                showDropTarget( x,
                                y,
                                width,
                                height );
                break;
            case WEST:
                x = dropTargetParent.getAbsoluteLeft();
                y = dropTargetParent.getAbsoluteTop();
                width = (int) ( dropTargetParent.getOffsetWidth() * 0.50 );
                height = dropTargetParent.getOffsetHeight();
                highlightActiveDropTarget( west );
                showDropTarget( x,
                        y,
                        width,
                        height );
                break;
            default:
                highlightActiveDropTarget( null );
                hideDropTarget();
        }
    }

    private void highlightActiveDropTarget(final Widget target){
        south.removeStyleName( FA_ACTIVE );
        north.removeStyleName( FA_ACTIVE );
        west.removeStyleName( FA_ACTIVE );
        east.removeStyleName( FA_ACTIVE );
        centre.removeStyleName( FA_ACTIVE );
        if( target != null ) {
            target.addStyleName( FA_ACTIVE );
        }
    }

    private void showDropTarget( int x,
                                 int y,
                                 int width,
                                 int height ) {
        dropTargetHighlight.getStyle().setLeft( x, Unit.PX );
        dropTargetHighlight.getStyle().setWidth( width, Unit.PX );
        dropTargetHighlight.getStyle().setTop( y, Unit.PX );
        dropTargetHighlight.getStyle().setHeight( height, Unit.PX );
        dropTargetHighlight.getStyle().setVisibility( Visibility.VISIBLE );
        dropTargetHighlight.getStyle().setDisplay( Display.BLOCK );
    }

    private void hideDropTarget() {
        dropTargetHighlight.getStyle().setVisibility( Visibility.HIDDEN );
        dropTargetHighlight.getStyle().setDisplay( Display.NONE );
        dropTargetPosition = CompassPosition.NONE;
    }

}
