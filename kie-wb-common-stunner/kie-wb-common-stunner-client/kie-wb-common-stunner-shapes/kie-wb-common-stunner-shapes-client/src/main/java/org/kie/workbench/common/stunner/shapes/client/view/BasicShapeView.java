/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.event.NodeMouseOverEvent;
import com.ait.lienzo.client.core.event.NodeMouseOverHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.event.*;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.AbstractShapeView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.event.*;
import org.kie.workbench.common.stunner.shapes.client.util.BasicShapesUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicShapeView<T>
        extends AbstractShapeView<T>
        implements
        HasTitle<T>,
        HasControlPoints<T>,
        HasEventHandlers<T, Shape<?>>,
        HasFillGradient<T>,
        HasChildren<BasicShapeView<T>> {

    private ViewEventHandlerManager eventHandlerManager;
    // Text event handlers will be only registered if text instace gets built.
    private ViewHandler<TextOverEvent> textOverHandlerViewHandler;
    private ViewHandler<TextOutEvent> textOutEventViewHandler;
    private final List<BasicShapeView<T>> children = new ArrayList<>();
    private Text text;

    private WiresLayoutContainer.Layout textPosition;
    private double textRotationDegrees;
    private Type fillGradientType = null;
    private String fillGradientStartColor = null;
    private String fillGradientEndColor = null;

    public BasicShapeView( final ViewEventType[] supportedEventTypes,
                           final MultiPath path ) {
        super( path );
        this.textPosition = WiresLayoutContainer.Layout.BOTTOM;
        this.textRotationDegrees = 0;
        initialize( supportedEventTypes );
    }

    @Override
    public void addChild( final BasicShapeView<T> child,
                          final Layout layout ) {
        children.add( child );
        super.addChild( ( IPrimitive<?> ) child.getContainer(), BasicShapesUtils.getWiresLayout( layout ) );
    }

    @Override
    public void removeChild( final BasicShapeView<T> child ) {
        children.remove( child );
        super.removeChild( ( IPrimitive<?> ) child.getContainer() );
    }

    @Override
    public Iterable<BasicShapeView<T>> getChildren() {
        return children;
    }

    @Override
    public boolean supports( final ViewEventType type ) {
        return eventHandlerManager.supports( type );
    }

    @Override
    public Shape<?> getAttachableShape() {
        return getShape();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitle( final String title ) {
        if ( null == text ) {
            text = buildText( title );
            this.addChild( text, getTextPosition() );
            registerTextOverHandler();
            registerTextOutHandler();
        } else {
            text.setText( title );
        }
        text.moveToTop();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitlePosition( final Position position ) {
        if ( Position.BOTTOM.equals( position ) ) {
            this.textPosition = LayoutContainer.Layout.BOTTOM;
        } else if ( Position.TOP.equals( position ) ) {
            this.textPosition = LayoutContainer.Layout.TOP;
        } else if ( Position.LEFT.equals( position ) ) {
            this.textPosition = LayoutContainer.Layout.LEFT;
        } else if ( Position.RIGHT.equals( position ) ) {
            this.textPosition = LayoutContainer.Layout.RIGHT;
        } else if ( Position.CENTER.equals( position ) ) {
            this.textPosition = LayoutContainer.Layout.CENTER;
        }
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitleRotation( final double degrees ) {
        this.textRotationDegrees = degrees;
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitleStrokeColor( final String color ) {
        text.setStrokeColor( color );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitleFontFamily( final String fontFamily ) {
        text.setFontFamily( fontFamily );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitleFontSize( final double fontSize ) {
        text.setFontSize( fontSize );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitleAlpha( final double alpha ) {
        text.setAlpha( alpha );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setTitleStrokeWidth( final double strokeWidth ) {
        text.setStrokeWidth( strokeWidth );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T moveTitleToTop() {
        text.moveToTop();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T refreshTitle() {
        if ( null != text ) {
            text.moveToTop();
        }
        return ( T ) this;
    }

    public Text getText() {
        return text;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setFillGradient( final Type type,
                              final String startColor,
                              final String endColor ) {
        this.fillGradientType = type;
        this.fillGradientStartColor = startColor;
        this.fillGradientEndColor = endColor;
        if ( null != getShape() ) {
            final BoundingBox bb = getShape().getBoundingBox();
            final double width = bb.getWidth();
            final double height = bb.getHeight();
            updateFillGradient( width, height );
        }
        return ( T ) this;
    }

    @SuppressWarnings( "unchecked" )
    public T updateFillGradient( final double width,
                                 final double height ) {
        if ( this.fillGradientType != null
                && this.fillGradientStartColor != null
                && this.fillGradientEndColor != null ) {
            final LinearGradient gradient = LienzoShapeUtils.getLinearGradient( fillGradientStartColor,
                    fillGradientEndColor, width, height );
            getShape().setFillGradient( gradient );
        }
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T showControlPoints( final ControlPointType type ) {
        IControlHandleList ctrls = loadControls( translate( type ) );
        if ( null != ctrls && ControlPointType.RESIZE.equals( type ) ) {
            // Apply this workaround for now when using the resize control points.
            ShapeControlPointsHelper.showOnlyLowerRightCP( ctrls );
        } else if ( null != ctrls ) {
            ctrls.show();
        }
        return ( T ) this;
    }

    private IControlHandle.ControlHandleType translate( final ControlPointType type ) {
        if ( type.equals( ControlPointType.RESIZE ) ) {
            return IControlHandle.ControlHandleStandardType.RESIZE;
        }
        return IControlHandle.ControlHandleStandardType.MAGNET;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T hideControlPoints() {
        IControlHandleList ctrls = getControls();
        if ( null != ctrls ) {
            ctrls.hide();
        }
        return ( T ) this;
    }

    @Override
    public boolean areControlsVisible() {
        return null != getControls() && getControls().isVisible();
    }

    @Override
    protected void doDestroy() {
        if ( null != eventHandlerManager ) {
            // Remove all registered handlers.
            eventHandlerManager.destroy();
            eventHandlerManager = null;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        // Nullify.
        this.text = null;
        this.textPosition = null;
        this.fillGradientEndColor = null;
        this.fillGradientStartColor = null;
        this.fillGradientType = null;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T addHandler( final ViewEventType type,
                         final ViewHandler<? extends ViewEvent> eventHandler ) {
        if ( supports( type ) ) {
            if ( ViewEventType.DRAG.equals( type ) ) {
                final HandlerRegistration[] registrations =
                        registerDragHandler( ( DragHandler ) eventHandler );
                if ( null != registrations ) {
                    eventHandlerManager.addHandlersRegistration( type, registrations );
                }
            } else if ( ViewEventType.RESIZE.equals( type ) ) {
                final HandlerRegistration[] registrations =
                        registerResizeHandler( ( ResizeHandler ) eventHandler );
                if ( null != registrations ) {
                    eventHandlerManager.addHandlersRegistration( type, registrations );
                }
            }
            if ( ViewEventType.TEXT_OVER.equals( type ) ) {
                textOverHandlerViewHandler = ( ViewHandler<TextOverEvent> ) eventHandler;
            }
            if ( ViewEventType.TEXT_OUT.equals( type ) ) {
                textOutEventViewHandler = ( ViewHandler<TextOutEvent> ) eventHandler;
            } else {
                eventHandlerManager.addHandler( type, eventHandler );
            }
        }
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T removeHandler( final ViewHandler<? extends ViewEvent> eventHandler ) {
        eventHandlerManager.removeHandler( eventHandler );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T enableHandlers() {
        eventHandlerManager.enable();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T disableHandlers() {
        eventHandlerManager.disable();
        return ( T ) this;
    }

    private void initialize( final ViewEventType[] supportedEventTypes ) {
        createEventHandlerManager( getGroup(), supportedEventTypes );
        refresh();
    }

    private void createEventHandlerManager( final Node<?> node,
                                            final ViewEventType[] supportedEventTypes ) {
        if ( null != node ) {
            this.eventHandlerManager = new ViewEventHandlerManager( node, supportedEventTypes );
        }
    }

    private Text buildText( String _text ) {
        Text text = new Text( _text )
                .setFontSize( 14 )
                .setFillColor( ColorName.BLACK )
                .setStrokeWidth( 1 )
                .setRotationDegrees( textRotationDegrees );
        return text.moveToTop().setDraggable( false ).setAlpha( 0 );
    }


    private WiresLayoutContainer.Layout getTextPosition() {
        return textPosition;
    }

    // TODO: listen for WiresMoveEvent's as well?
    private HandlerRegistration[] registerDragHandler( final ViewHandler<DragEvent> eventHandler ) {
        if ( !getAttachableShape().isDraggable() ) {
            final DragHandler dragHandler = ( DragHandler ) eventHandler;
            setDraggable( true );
            HandlerRegistration dragStartReg = addWiresDragStartHandler( wiresDragStartEvent -> {
                final DragEvent e = buildDragEvent( wiresDragStartEvent );
                dragHandler.start( e );
            } );
            HandlerRegistration dragMoveReg = addWiresDragMoveHandler( wiresDragMoveEvent -> {
                final DragEvent e = buildDragEvent( wiresDragMoveEvent );
                dragHandler.handle( e );
            } );
            HandlerRegistration dragEndReg = addWiresDragEndHandler( wiresDragEndEvent -> {
                final DragEvent e = buildDragEvent( wiresDragEndEvent );
                dragHandler.end( e );
            } );
            return new HandlerRegistration[]{ dragStartReg, dragMoveReg, dragEndReg };
        }
        return null;
    }

    private void registerTextOverHandler() {
        if ( null != textOverHandlerViewHandler ) {
            HandlerRegistration registration = getText().addNodeMouseOverHandler( new NodeMouseOverHandler() {
                @Override
                public void onNodeMouseOver( NodeMouseOverEvent nodeMouseOverEvent ) {
                    final TextOverEvent event = new TextOverEvent( nodeMouseOverEvent.getX(), nodeMouseOverEvent.getY(),
                            nodeMouseOverEvent.getMouseEvent().getClientX(), nodeMouseOverEvent.getMouseEvent().getClientY() );
                    textOverHandlerViewHandler.handle( event );
                }
            } );
            eventHandlerManager.addHandlersRegistration( ViewEventType.TEXT_OVER, registration );
        }
    }

    private void registerTextOutHandler() {
        if ( null != textOutEventViewHandler ) {
            HandlerRegistration registration = getText().addNodeMouseOutHandler( nodeMouseOverEvent -> {
                final TextOutEvent event = new TextOutEvent( nodeMouseOverEvent.getX(), nodeMouseOverEvent.getY(),
                        nodeMouseOverEvent.getMouseEvent().getClientX(), nodeMouseOverEvent.getMouseEvent().getClientY() );
                textOutEventViewHandler.handle( event );
            } );
            eventHandlerManager.addHandlersRegistration( ViewEventType.TEXT_OUT, registration );
        }
    }

    private HandlerRegistration[] registerResizeHandler( final ViewHandler<ResizeEvent> eventHandler ) {
        final ResizeHandler resizeHandler = ( ResizeHandler ) eventHandler;
        setResizable( true );
        HandlerRegistration r0 = addWiresResizeStartHandler( new WiresResizeStartHandler() {
            @Override
            public void onShapeResizeStart( WiresResizeStartEvent wiresResizeStartEvent ) {
                final ResizeEvent event = buildResizeEvent( wiresResizeStartEvent );
                resizeHandler.start( event );
            }
        } );
        HandlerRegistration r1 = addWiresResizeStepHandler( new WiresResizeStepHandler() {
            @Override
            public void onShapeResizeStep( WiresResizeStepEvent wiresResizeStepEvent ) {
                final ResizeEvent event = buildResizeEvent( wiresResizeStepEvent );
                resizeHandler.handle( event );
            }
        } );
        HandlerRegistration r2 = addWiresResizeEndHandler( new WiresResizeEndHandler() {
            @Override
            public void onShapeResizeEnd( WiresResizeEndEvent wiresResizeEndEvent ) {
                final ResizeEvent event = buildResizeEvent( wiresResizeEndEvent );
                resizeHandler.end( event );
            }
        } );
        return new HandlerRegistration[]{ r0, r1, r2 };
    }

    private DragEvent buildDragEvent( final AbstractWiresDragEvent sourceDragEvent ) {
        final double x = sourceDragEvent.getX();
        final double y = sourceDragEvent.getY();
        final double cx = sourceDragEvent.getNodeDragEvent().getX();
        final double cy = sourceDragEvent.getNodeDragEvent().getY();
        final int dx = sourceDragEvent.getNodeDragEvent().getDragContext().getDx();
        final int dy = sourceDragEvent.getNodeDragEvent().getDragContext().getDy();
        return new DragEvent( x, y, cx, cy, dx, dy );
    }

    private ResizeEvent buildResizeEvent( final AbstractWiresResizeEvent sourceResizeEvent ) {
        final double x = sourceResizeEvent.getX();
        final double y = sourceResizeEvent.getY();
        final double cx = sourceResizeEvent.getNodeDragEvent().getX();
        final double cy = sourceResizeEvent.getNodeDragEvent().getY();
        final double w = sourceResizeEvent.getWidth();
        final double h = sourceResizeEvent.getHeight();
        return new ResizeEvent( x, y, cx, cy, w, h );
    }
}
