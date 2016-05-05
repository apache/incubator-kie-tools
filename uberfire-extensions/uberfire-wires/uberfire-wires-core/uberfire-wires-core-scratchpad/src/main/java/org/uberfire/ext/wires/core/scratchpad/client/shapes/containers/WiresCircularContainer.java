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
package org.uberfire.ext.wires.core.scratchpad.client.shapes.containers;

import com.ait.lienzo.client.core.shape.Circle;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPoint;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.uberfire.ext.wires.core.api.magnets.Magnet;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseDynamicContainer;
import org.uberfire.ext.wires.core.client.controlpoints.DefaultControlPoint;
import org.uberfire.ext.wires.core.client.magnets.DefaultMagnet;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

public class WiresCircularContainer extends WiresBaseDynamicContainer {

    private static final int BOUNDARY_SIZE = 10;

    private final Circle circle;
    private final Circle bounding;
    private final String circleStrokeColour;
    private final String circleFillColour;

    private final Magnet magnet1;
    private final Magnet magnet2;
    private final Magnet magnet3;
    private final Magnet magnet4;

    private final ControlPoint controlPoint1;

    public WiresCircularContainer( final Circle shape ) {
        circle = shape;
        circleFillColour = shape.getFillColor();
        circleStrokeColour = shape.getStrokeColor();

        final double radius = circle.getRadius();
        bounding = new Circle( radius + ( BOUNDARY_SIZE / 2 ) );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        add( circle );

        magnets.clear();
        magnet1 = new DefaultMagnet( getX() - radius,
                                     getY() );
        magnet2 = new DefaultMagnet( getX() + radius,
                                     getY() );
        magnet3 = new DefaultMagnet( getX(),
                                     getY() - radius );
        magnet4 = new DefaultMagnet( getX(),
                                     getY() + radius );
        addMagnet( magnet1 );
        addMagnet( magnet2 );
        addMagnet( magnet3 );
        addMagnet( magnet4 );

        controlPoints.clear();
        controlPoint1 = new DefaultControlPoint( getX() + radius,
                                                 getY(),
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( final double x,
                                                                         final double y ) {
                                                         final double dx = getX() - x;
                                                         final double dy = getY() - y;
                                                         final double r = Math.sqrt( Math.pow( dx, 2 ) + Math.pow( dy, 2 ) );
                                                         magnet1.setX( getX() - r );
                                                         magnet2.setX( getX() + r );
                                                         magnet3.setY( getY() - r );
                                                         magnet4.setY( getY() + r );
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

    @Override
    public void setHover( final boolean isHover ) {
        if ( isHover ) {
            circle.setFillColor( ShapesUtils.RGB_FILL_HOVER_CONTAINER );
            circle.setStrokeColor( ShapesUtils.RGB_STROKE_HOVER_CONTAINER );
        } else {
            circle.setFillColor( circleFillColour );
            circle.setStrokeColor( circleStrokeColour );
        }
    }

    @Override
    public String toString() {
        return "WiresCircularContainer{" + "id=" + getId() + ",x = " + getX() + ", y = " + getY() + "}";
    }

}
