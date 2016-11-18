/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;

public abstract class AbstractConnectorView<T> extends WiresConnector
        implements
        ShapeView<T>,
        IsConnector<T>,
        HasControlPoints<T> {

    protected String uuid;
    private int zindex;
    private WiresConnectorControl connectorControl;
    private final HandlerRegistrationImpl handlerRegistration = new HandlerRegistrationImpl();

    public AbstractConnectorView( AbstractDirectionalMultiPointShape<?> line, MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator ) {
        super( line, headDecorator, tailDecorator );
        init();
    }

    public AbstractConnectorView( WiresMagnet headMagnet, WiresMagnet tailMagnet, AbstractDirectionalMultiPointShape<?> line,
                                  MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator ) {
        super( headMagnet, tailMagnet, line, headDecorator, tailDecorator );
        init();
    }

    protected abstract void doDestroy();

    protected void init() {
        getLine().setFillColor( ColorName.WHITE ).setStrokeWidth( 0 );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setUUID( final String uuid ) {
        this.uuid = uuid;
        this.getGroup().setUserData( UUID_PREFIX + uuid );
        return ( T ) this;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setZIndex( final int zindez ) {
        this.zindex = zindez;
        return ( T ) this;
    }

    @Override
    public int getZIndex() {
        return zindex;
    }

    @SuppressWarnings( "unchecked" )
    public T setControl( final WiresConnectorControl connectorControl ) {
        this.connectorControl = connectorControl;
        return ( T ) this;
    }

    public WiresConnectorControl getControl() {
        return connectorControl;
    }

    @SuppressWarnings( "unchecked" )
    public T connect( final ShapeView headShapeView,
                      final int _headMagnetsIndex,
                      final ShapeView tailShapeView,
                      final int _tailMagnetsIndex,
                      final boolean tailArrow,
                      final boolean headArrow ) {
        final WiresShape headWiresShape = ( WiresShape ) headShapeView;
        final WiresShape tailWiresShape = ( WiresShape ) tailShapeView;
        final MagnetManager.Magnets headMagnets = headWiresShape.getMagnets();
        final MagnetManager.Magnets tailMagnets = tailWiresShape.getMagnets();
        int headMagnetsIndex = _headMagnetsIndex;
        int tailMagnetsIndex = _tailMagnetsIndex;
        if ( headMagnetsIndex < 0 ) {
            headMagnetsIndex = 0;
        }
        if ( tailMagnetsIndex < 0 ) {
            tailMagnetsIndex = 0;
        }
        // Obtain the magnets.
        WiresMagnet m0_1 = headMagnets.getMagnet( headMagnetsIndex );
        WiresMagnet m1_1 = tailMagnets.getMagnet( tailMagnetsIndex );
        // Update the magnets.
        this.setHeadMagnet( m0_1 );
        this.setTailMagnet( m1_1 );
        return ( T ) this;
    }

    private OrthogonalPolyLine createLine( final double... points ) {
        return new OrthogonalPolyLine( Point2DArray.fromArrayOfDouble( points ) ).setCornerRadius( 5 ).setDraggable( true );
    }

    @Override
    public void removeFromParent() {
        // Remove the main line.
        super.removeFromLayer();
    }

    @Override
    public double getShapeX() {
        return getGroup().getX();
    }

    @Override
    public double getShapeY() {
        return getGroup().getY();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setShapeX( final double x ) {
        getGroup().setX( x );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setShapeY( final double y ) {
        getGroup().setY( y );
        return ( T ) this;
    }

    @Override
    public double[] getShapeAbsoluteLocation() {
        return WiresUtils.getAbsolute( getGroup() );
    }

    @Override
    public String getFillColor() {
        return getLine().getFillColor();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setFillColor( final String color ) {
        getLine().setFillColor( color );
        return ( T ) this;
    }

    @Override
    public double getFillAlpha() {
        return getLine().getFillAlpha();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setFillAlpha( final double alpha ) {
        getLine().setFillAlpha( alpha );
        return ( T ) this;
    }

    @Override
    public String getStrokeColor() {
        return getLine().getStrokeColor();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setStrokeColor( final String color ) {
        getLine().setStrokeColor( color );
        return ( T ) this;
    }

    @Override
    public double getStrokeAlpha() {
        return getLine().getStrokeAlpha();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setStrokeAlpha( final double alpha ) {
        getLine().setStrokeAlpha( alpha );
        return ( T ) this;
    }

    @Override
    public double getStrokeWidth() {
        return getLine().getStrokeWidth();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setStrokeWidth( final double width ) {
        getLine().setStrokeWidth( width );
        return ( T ) this;
    }

    // TODO: Move this into lienzo WiresShape/WiresConnector?
    @Override
    @SuppressWarnings( "unchecked" )
    public T setDragBounds( final double x1,
                            final double y1,
                            final double x2,
                            final double y2 ) {
        getGroup().setDragBounds( new DragBounds( x1, y1, x2, y2 ) );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T moveToTop() {
        getGroup().moveToTop();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T moveToBottom() {
        getGroup().moveToBottom();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T moveUp() {
        getGroup().moveUp();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T moveDown() {
        getGroup().moveDown();
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T showControlPoints( final ControlPointType type ) {
        if ( null != getControl() ) {
            if ( ControlPointType.MAGNET.equals( type ) ) {
                getControl().showControlPoints();
            } else {
                throw new UnsupportedOperationException( "Control point type [" + type + "] not supported yet" );
            }
        }
        return ( T ) this;
    }


    @Override
    @SuppressWarnings( "unchecked" )
    public T hideControlPoints() {
        if ( null != getControl() ) {
            getControl().hideControlPoints();
        }
        return ( T ) this;
    }

    @Override
    public boolean areControlsVisible() {
        return getPointHandles().isVisible();
    }

    @Override
    public void destroy() {
        // Remove any handler registrations present.
        handlerRegistration.removeHandler();
        // Implementations can clear its state here.
        this.doDestroy();
        // Remove me.
        this.removeFromParent();
        this.connectorControl = null;
    }

    /**
     * Try to make easier the connectors selection/updates from a user perspective ( eg: when line widths are small )
     */
    protected void enableShowControlsOnMouseEnter() {
        // Register a mouse enter handler for the connector's line.
        handlerRegistration.register(
                getLine().addNodeMouseEnterHandler( nodeMouseEnterEvent -> showControlPoints( ControlPointType.MAGNET ) )
        );
        // Register a mouse enter handler for the connector's head decorator, if exists.
        if ( null != getHead() ) {
            handlerRegistration.register(
                    getHead().addNodeMouseEnterHandler( nodeMouseEnterEvent -> showControlPoints( ControlPointType.MAGNET ) )
            );
        }
        // Register a mouse enter handler for the connector's tail decorator, if exists.
        if ( null != getTail() ) {
            handlerRegistration.register(
                    getTail().addNodeMouseEnterHandler( nodeMouseEnterEvent -> showControlPoints( ControlPointType.MAGNET ) )
            );
        }
    }

}
