/*
 * Copyright 2014 JBoss Inc
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
package org.kie.uberfire.wires.core.scratchpad.client.shapes.dynamic;

import com.emitrom.lienzo.client.core.shape.Circle;
import org.kie.uberfire.wires.core.api.controlpoints.ControlPoint;
import org.kie.uberfire.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.kie.uberfire.wires.core.api.magnets.Magnet;
import org.kie.uberfire.wires.core.client.controlpoints.DefaultControlPoint;
import org.kie.uberfire.wires.core.client.magnets.DefaultMagnet;
import org.kie.uberfire.wires.core.scratchpad.client.shapes.WiresScratchPadDefaultShape;

public class WiresCircle extends WiresScratchPadDefaultShape {

    private static final int BOUNDARY_SIZE = 10;

    private Circle circle;
    private Circle bounding;

    private final Magnet magnet1;
    private final Magnet magnet2;
    private final Magnet magnet3;
    private final Magnet magnet4;

    private final ControlPoint controlPoint1;

    public WiresCircle( final Circle shape ) {
        final double radius = shape.getRadius();

        circle = shape;
        bounding = new Circle( radius + ( BOUNDARY_SIZE / 2 ) );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        add( circle );

        magnets.clear();
        magnet1 = new DefaultMagnet( 0 - radius,
                                     0 );
        magnet2 = new DefaultMagnet( radius,
                                     0 );
        magnet3 = new DefaultMagnet( 0,
                                     0 - radius );
        magnet4 = new DefaultMagnet( 0,
                                     radius );
        addMagnet( magnet1 );
        addMagnet( magnet2 );
        addMagnet( magnet3 );
        addMagnet( magnet4 );

        controlPoints.clear();
        controlPoint1 = new DefaultControlPoint( radius,
                                                 0,
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( final double x,
                                                                         final double y ) {
                                                         final double r = Math.sqrt( Math.pow( x, 2 ) + Math.pow( y, 2 ) );
                                                         magnet1.setX( 0 - r );
                                                         magnet2.setX( r );
                                                         magnet3.setY( 0 - r );
                                                         magnet4.setY( r );
                                                         circle.setRadius( r );
                                                         bounding.setRadius( r + ( BOUNDARY_SIZE / 2 ) );
                                                     }
                                                 }
        );
        addControlPoint( controlPoint1 );
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
        return Math.sqrt( Math.pow( _x, 2 ) + Math.pow( _y, 2 ) ) < circle.getRadius() + BOUNDARY_SIZE;
    }

    public double getRadius() {
        return circle.getRadius();
    }

    public void setRadius( final double radius ) {
        circle.setRadius( radius );
        bounding.setRadius( radius + ( BOUNDARY_SIZE / 2 ) );
        final double theta = Math.atan( controlPoint1.getY() / controlPoint1.getX() );
        controlPoint1.setX( radius * Math.cos( theta ) );
        controlPoint1.setY( radius * Math.sin( theta ) );
    }

}
