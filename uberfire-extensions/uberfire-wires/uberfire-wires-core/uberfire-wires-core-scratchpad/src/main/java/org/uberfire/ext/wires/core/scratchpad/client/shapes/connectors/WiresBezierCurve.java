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
package org.uberfire.ext.wires.core.scratchpad.client.shapes.connectors;

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.shape.BezierCurve;
import com.ait.lienzo.client.core.shape.Line;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPoint;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.uberfire.ext.wires.core.api.magnets.Magnet;
import org.uberfire.ext.wires.core.api.magnets.MagnetManager;
import org.uberfire.ext.wires.core.api.magnets.RequiresMagnetManager;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseDynamicShape;
import org.uberfire.ext.wires.core.api.shapes.WiresShape;
import org.uberfire.ext.wires.core.client.controlpoints.ConnectibleControlPoint;
import org.uberfire.ext.wires.core.client.controlpoints.DefaultControlPoint;

public class WiresBezierCurve extends WiresBaseDynamicShape implements MagnetManager,
                                                                       RequiresMagnetManager {

    private static final int BOUNDARY_SIZE = 10;

    //We do not hide the boundary item for Lines as it makes selecting them very difficult
    private static final double ALPHA_DESELECTED = 0.01;
    private static final double ALPHA_SELECTED = 0.1;

    private final BezierCurve curve;
    private final BezierCurve bounding;
    private final Line controlLine1;
    private final Line controlLine2;

    private final ConnectibleControlPoint controlPoint1;
    private final ControlPoint controlPoint2;
    private final ControlPoint controlPoint3;
    private final ConnectibleControlPoint controlPoint4;

    private MagnetManager magnetManager;

    public WiresBezierCurve( final BezierCurve shape ) {
        final double x = shape.getControlPoints().get( 0 ).getX();
        final double y = shape.getControlPoints().get( 0 ).getY();
        final double controlX1 = shape.getControlPoints().get( 1 ).getX();
        final double controlY1 = shape.getControlPoints().get( 1 ).getY();
        final double controlX2 = shape.getControlPoints().get( 2 ).getX();
        final double controlY2 = shape.getControlPoints().get( 2 ).getY();
        final double endX = shape.getControlPoints().get( 3 ).getX();
        final double endY = shape.getControlPoints().get( 3 ).getY();

        curve = shape;
        bounding = new BezierCurve( x,
                                    y,
                                    controlX1,
                                    controlY1,
                                    controlX2,
                                    controlY2,
                                    endX,
                                    endY );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( ALPHA_DESELECTED );

        controlLine1 = new Line( x,
                                 y,
                                 controlX1,
                                 controlY1 );
        controlLine1.setAlpha( 0.5 );
        controlLine1.setStrokeColor( "#0000ff" );
        controlLine1.setDashArray( 2, 2 );
        controlLine2 = new Line( controlX2,
                                 controlY2,
                                 endX,
                                 endY );
        controlLine2.setAlpha( 0.5 );
        controlLine2.setStrokeColor( "#0000ff" );
        controlLine2.setDashArray( 2, 2 );

        add( curve );
        add( bounding );

        magnets.clear();

        controlPoints.clear();
        controlPoint1 = new ConnectibleControlPoint( curve.getControlPoints().get( 0 ).getX(),
                                                     curve.getControlPoints().get( 0 ).getY(),
                                                     this,
                                                     this,
                                                     new ControlPointMoveHandler() {
                                                         @Override
                                                         public void onMove( final double x,
                                                                             final double y ) {
                                                             curve.getControlPoints().get( 0 ).setX( x - getX() );
                                                             curve.getControlPoints().get( 0 ).setY( y - getY() );
                                                             bounding.getControlPoints().get( 0 ).setX( x - getX() );
                                                             bounding.getControlPoints().get( 0 ).setY( y - getY() );
                                                             controlLine1.getPoints().get( 0 ).setX( x - getX() );
                                                             controlLine1.getPoints().get( 0 ).setY( y - getY() );
                                                         }
                                                     }
        );

        controlPoint2 = new DefaultControlPoint( curve.getControlPoints().get( 1 ).getX(),
                                                 curve.getControlPoints().get( 1 ).getY(),
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( final double x,
                                                                         final double y ) {
                                                         curve.getControlPoints().get( 1 ).setX( x - getX() );
                                                         curve.getControlPoints().get( 1 ).setY( y - getY() );
                                                         bounding.getControlPoints().get( 1 ).setX( x - getX() );
                                                         bounding.getControlPoints().get( 1 ).setY( y - getY() );
                                                         controlLine1.getPoints().get( 1 ).setX( x - getX() );
                                                         controlLine1.getPoints().get( 1 ).setY( y - getY() );
                                                     }
                                                 }
        );

        controlPoint3 = new DefaultControlPoint( curve.getControlPoints().get( 2 ).getX(),
                                                 curve.getControlPoints().get( 2 ).getY(),
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( final double x,
                                                                         final double y ) {
                                                         curve.getControlPoints().get( 2 ).setX( x - getX() );
                                                         curve.getControlPoints().get( 2 ).setY( y - getY() );
                                                         bounding.getControlPoints().get( 2 ).setX( x - getX() );
                                                         bounding.getControlPoints().get( 2 ).setY( y - getY() );
                                                         controlLine2.getPoints().get( 0 ).setX( x - getX() );
                                                         controlLine2.getPoints().get( 0 ).setY( y - getY() );
                                                     }
                                                 }
        );

        controlPoint4 = new ConnectibleControlPoint( curve.getControlPoints().get( 3 ).getX(),
                                                     curve.getControlPoints().get( 3 ).getY(),
                                                     this,
                                                     this,
                                                     new ControlPointMoveHandler() {
                                                         @Override
                                                         public void onMove( final double x,
                                                                             final double y ) {
                                                             curve.getControlPoints().get( 3 ).setX( x - getX() );
                                                             curve.getControlPoints().get( 3 ).setY( y - getY() );
                                                             bounding.getControlPoints().get( 3 ).setX( x - getX() );
                                                             bounding.getControlPoints().get( 3 ).setY( y - getY() );
                                                             controlLine2.getPoints().get( 1 ).setX( x - getX() );
                                                             controlLine2.getPoints().get( 1 ).setY( y - getY() );
                                                         }
                                                     }
        );

        addControlPoint( controlPoint1 );
        addControlPoint( controlPoint2 );
        addControlPoint( controlPoint3 );
        addControlPoint( controlPoint4 );

        //If Connector is dragged as a whole (i.e. not a ControlPoint) detach it from Magnets
        addNodeDragMoveHandler( new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                final Magnet boundMagnet1 = controlPoint1.getBoundMagnet();
                final Magnet boundMagnet4 = controlPoint4.getBoundMagnet();
                if ( boundMagnet1 != null ) {
                    boundMagnet1.detachControlPoint( controlPoint1 );
                }
                if ( boundMagnet4 != null ) {
                    boundMagnet4.detachControlPoint( controlPoint4 );
                }
                getLayer().batch();
            }
        } );
    }

    @Override
    public void setMagnetManager( final MagnetManager magnetManager ) {
        this.magnetManager = magnetManager;
    }

    @Override
    public void hideAllMagnets() {
        if ( magnetManager != null ) {
            magnetManager.hideAllMagnets();
        }
    }

    @Override
    public Magnet getMagnet( final WiresShape shapeActive,
                             final double cx,
                             final double cy ) {
        if ( this.magnetManager != null ) {
            return magnetManager.getMagnet( shapeActive,
                                            cx,
                                            cy );
        }
        return null;
    }

    @Override
    public void setSelected( final boolean isSelected ) {
        if ( isSelected ) {
            add( controlLine1 );
            add( controlLine2 );
            bounding.setAlpha( ALPHA_SELECTED );
        } else {
            remove( controlLine1 );
            remove( controlLine2 );
            bounding.setAlpha( ALPHA_DESELECTED );
        }
    }

    @Override
    public boolean contains( final double cx,
                             final double cy ) {
        return false;
    }

}
