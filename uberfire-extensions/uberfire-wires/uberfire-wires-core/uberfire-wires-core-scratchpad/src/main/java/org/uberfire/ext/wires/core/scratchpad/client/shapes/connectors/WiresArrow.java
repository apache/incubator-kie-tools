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
import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowType;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.uberfire.ext.wires.core.api.magnets.Magnet;
import org.uberfire.ext.wires.core.api.magnets.MagnetManager;
import org.uberfire.ext.wires.core.api.magnets.RequiresMagnetManager;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseDynamicShape;
import org.uberfire.ext.wires.core.api.shapes.WiresShape;
import org.uberfire.ext.wires.core.client.controlpoints.ConnectibleControlPoint;
import org.uberfire.ext.wires.core.client.util.GeometryUtil;

public class WiresArrow extends WiresBaseDynamicShape implements MagnetManager,
                                                                 RequiresMagnetManager {

    private static final int BOUNDARY_SIZE = 10;

    private static final int BASE_WIDTH = 10;
    private static final int HEAD_WIDTH = 20;
    private static final int ARROW_ANGLE = 45;
    private static final int BASE_ANGLE = 30;

    private final Arrow arrow;
    private final Arrow bounding;

    private final ConnectibleControlPoint controlPoint1;
    private final ConnectibleControlPoint controlPoint2;

    private MagnetManager magnetManager;

    public WiresArrow( final Arrow shape ) {
        final double x1 = shape.getStart().getX();
        final double y1 = shape.getStart().getY();
        final double x2 = shape.getEnd().getX();
        final double y2 = shape.getEnd().getY();

        arrow = shape;
        bounding = new Arrow( new Point2D( x1,
                                           y1 ),
                              new Point2D( x2,
                                           y2 ),
                              BASE_WIDTH,
                              HEAD_WIDTH,
                              ARROW_ANGLE,
                              BASE_ANGLE,
                              ArrowType.AT_END );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        add( arrow );

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
                                                             arrow.setStart( new Point2D( x - getX(),
                                                                                          y - getY() ) );
                                                             bounding.setStart( new Point2D( x - getX(),
                                                                                             y - getY() ) );
                                                         }
                                                     }
        );

        controlPoint2 = new ConnectibleControlPoint( x2,
                                                     y2,
                                                     this,
                                                     this,
                                                     new ControlPointMoveHandler() {
                                                         @Override
                                                         public void onMove( final double x,
                                                                             final double y ) {
                                                             arrow.setEnd( new Point2D( x - getX(),
                                                                                        y - getY() ) );
                                                             bounding.setEnd( new Point2D( x - getX(),
                                                                                           y - getY() ) );
                                                         }
                                                     }
        );
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
            add( bounding );
        } else {
            remove( bounding );
        }
    }

    @Override
    public boolean contains( final double cx,
                             final double cy ) {
        final double _x = cx - getX();
        final double _y = cy - getY();
        return Math.sqrt( GeometryUtil.ptSegDistSq( arrow.getPoints().get( 0 ).getX(),
                                                    arrow.getPoints().get( 0 ).getY(),
                                                    arrow.getPoints().get( 1 ).getX(),
                                                    arrow.getPoints().get( 1 ).getY(),
                                                    _x,
                                                    _y ) ) < BOUNDARY_SIZE;
    }

    @Override
    public String toString() {
        return "WiresArrow{" + "id=" + getId() + ",x = " + getX() + ", y = " + getY() + "}";
    }
}
