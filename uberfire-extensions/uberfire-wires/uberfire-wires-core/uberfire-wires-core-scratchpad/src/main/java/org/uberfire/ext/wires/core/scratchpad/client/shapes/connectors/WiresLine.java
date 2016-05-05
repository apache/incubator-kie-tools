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
import com.ait.lienzo.client.core.shape.Line;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.uberfire.ext.wires.core.api.magnets.Magnet;
import org.uberfire.ext.wires.core.api.magnets.MagnetManager;
import org.uberfire.ext.wires.core.api.magnets.RequiresMagnetManager;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseDynamicShape;
import org.uberfire.ext.wires.core.api.shapes.WiresShape;
import org.uberfire.ext.wires.core.client.controlpoints.ConnectibleControlPoint;
import org.uberfire.ext.wires.core.client.util.GeometryUtil;

public class WiresLine extends WiresBaseDynamicShape implements MagnetManager,
                                                                RequiresMagnetManager {

    private static final int BOUNDARY_SIZE = 10;

    //We do not hide the boundary item for Lines as it makes selecting them very difficult
    private static final double ALPHA_DESELECTED = 0.01;
    private static final double ALPHA_SELECTED = 0.1;

    private final Line line;
    private final Line bounding;

    private final ConnectibleControlPoint controlPoint1;
    private final ConnectibleControlPoint controlPoint2;

    private MagnetManager magnetManager;

    public WiresLine( final Line shape ) {
        final double x1 = shape.getPoints().get( 0 ).getX();
        final double y1 = shape.getPoints().get( 0 ).getY();
        final double x2 = shape.getPoints().get( 1 ).getX();
        final double y2 = shape.getPoints().get( 1 ).getY();

        line = shape;
        bounding = new Line( x1,
                             y1,
                             x2,
                             y2 );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( ALPHA_DESELECTED );

        add( line );
        add( bounding );

        magnets.clear();

        controlPoints.clear();
        controlPoint1 = new ConnectibleControlPoint( x1,
                                                     y1,
                                                     this,
                                                     this,
                                                     new ControlPointMoveHandler() {
                                                         @Override
                                                         public void onMove( final double x,
                                                                             final double y ) {
                                                             line.getPoints().get( 0 ).setX( x - getX() );
                                                             line.getPoints().get( 0 ).setY( y - getY() );
                                                             bounding.getPoints().get( 0 ).setX( x - getX() );
                                                             bounding.getPoints().get( 0 ).setY( y - getY() );
                                                         }
                                                     } );

        controlPoint2 = new ConnectibleControlPoint( x2,
                                                     y2,
                                                     this,
                                                     this,
                                                     new ControlPointMoveHandler() {
                                                         @Override
                                                         public void onMove( final double x,
                                                                             final double y ) {
                                                             line.getPoints().get( 1 ).setX( x - getX() );
                                                             line.getPoints().get( 1 ).setY( y - getY() );
                                                             bounding.getPoints().get( 1 ).setX( x - getX() );
                                                             bounding.getPoints().get( 1 ).setY( y - getY() );
                                                         }
                                                     } );
        addControlPoint( controlPoint1 );
        addControlPoint( controlPoint2 );

        //If Connector is dragged as a whole (i.e. not a ControlPoint) detach it from Magnets
        addNodeDragMoveHandler( new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                final Magnet boundMagnet1 = controlPoint1.getBoundMagnet();
                final Magnet boundMagnet2 = controlPoint2.getBoundMagnet();
                if ( boundMagnet1 != null ) {
                    boundMagnet1.detachControlPoint( controlPoint1 );
                }
                if ( boundMagnet2 != null ) {
                    boundMagnet2.detachControlPoint( controlPoint2 );
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
            bounding.setAlpha( ALPHA_SELECTED );
        } else {
            bounding.setAlpha( ALPHA_DESELECTED );
        }
    }

    @Override
    public boolean contains( final double cx,
                             final double cy ) {
        final double _x = cx - getX();
        final double _y = cy - getY();
        return Math.sqrt( GeometryUtil.ptSegDistSq( line.getPoints().get( 0 ).getX(),
                                                    line.getPoints().get( 0 ).getY(),
                                                    line.getPoints().get( 1 ).getX(),
                                                    line.getPoints().get( 1 ).getY(),
                                                    _x,
                                                    _y ) ) < BOUNDARY_SIZE;
    }

    @Override
    public String toString() {
        return "WiresLine{" + "id=" + getId() + ",x = " + getX() + ", y = " + getY() + "}";
    }
}
