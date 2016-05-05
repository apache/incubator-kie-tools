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

import com.ait.lienzo.client.core.shape.Rectangle;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPoint;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseDynamicContainer;
import org.uberfire.ext.wires.core.client.controlpoints.DefaultControlPoint;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

public class WiresRectangularContainer extends WiresBaseDynamicContainer {

    private static final int BOUNDARY_SIZE = 10;

    private final Rectangle rectangle;
    private final Rectangle bounding;
    private final String rectangleFillColour;
    private final String rectangleStokeColour;

    private final ControlPoint controlPoint1;
    private final ControlPoint controlPoint2;
    private final ControlPoint controlPoint3;
    private final ControlPoint controlPoint4;

    public WiresRectangularContainer( final Rectangle shape ) {
        final double x1 = shape.getX();
        final double y1 = shape.getY();
        final double x2 = shape.getX() + shape.getWidth();
        final double y2 = shape.getY() + shape.getHeight();
        final double width = Math.abs( x2 - x1 );
        final double height = Math.abs( y2 - y1 );

        rectangle = shape;
        rectangleFillColour = shape.getFillColor();
        rectangleStokeColour = shape.getStrokeColor();

        bounding = new Rectangle( width + BOUNDARY_SIZE,
                                  height + BOUNDARY_SIZE,
                                  rectangle.getCornerRadius() );
        bounding.setX( x1 - ( BOUNDARY_SIZE / 2 ) );
        bounding.setY( y1 - ( BOUNDARY_SIZE / 2 ) );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        add( rectangle );

        magnets.clear();
        controlPoints.clear();

        final double px1 = rectangle.getX();
        final double py1 = rectangle.getY();
        controlPoint1 = new DefaultControlPoint( px1,
                                                 py1,
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( final double x,
                                                                         final double y ) {
                                                         controlPoint2.setY( controlPoint1.getY() );
                                                         controlPoint3.setX( controlPoint1.getX() );
                                                         rectangle.setX( x - getX() );
                                                         rectangle.setY( y - getY() );
                                                         rectangle.setWidth( controlPoint2.getX() - controlPoint1.getX() );
                                                         rectangle.setHeight( controlPoint3.getY() - controlPoint1.getY() );
                                                         bounding.setX( rectangle.getX() - ( BOUNDARY_SIZE / 2 ) );
                                                         bounding.setY( rectangle.getY() - ( BOUNDARY_SIZE / 2 ) );
                                                         bounding.setWidth( rectangle.getWidth() + BOUNDARY_SIZE );
                                                         bounding.setHeight( rectangle.getHeight() + BOUNDARY_SIZE );
                                                     }
                                                 }
        );

        final double px2 = rectangle.getX() + rectangle.getWidth();
        final double py2 = rectangle.getY();
        controlPoint2 = new DefaultControlPoint( px2,
                                                 py2,
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( double x,
                                                                         double y ) {
                                                         controlPoint1.setY( controlPoint2.getY() );
                                                         controlPoint4.setX( controlPoint2.getX() );
                                                         rectangle.setY( y - getY() );
                                                         rectangle.setWidth( controlPoint2.getX() - controlPoint1.getX() );
                                                         rectangle.setHeight( controlPoint3.getY() - controlPoint1.getY() );
                                                         bounding.setY( rectangle.getY() - ( BOUNDARY_SIZE / 2 ) );
                                                         bounding.setWidth( rectangle.getWidth() + BOUNDARY_SIZE );
                                                         bounding.setHeight( rectangle.getHeight() + BOUNDARY_SIZE );
                                                     }
                                                 }
        );

        final double px3 = rectangle.getX();
        final double py3 = rectangle.getY() + rectangle.getHeight();
        controlPoint3 = new DefaultControlPoint( px3,
                                                 py3,
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( double x,
                                                                         double y ) {
                                                         controlPoint1.setX( controlPoint3.getX() );
                                                         controlPoint4.setY( controlPoint3.getY() );
                                                         rectangle.setX( x - getX() );
                                                         rectangle.setWidth( controlPoint2.getX() - controlPoint1.getX() );
                                                         rectangle.setHeight( controlPoint3.getY() - controlPoint1.getY() );
                                                         bounding.setX( rectangle.getX() - ( BOUNDARY_SIZE / 2 ) );
                                                         bounding.setWidth( rectangle.getWidth() + BOUNDARY_SIZE );
                                                         bounding.setHeight( rectangle.getHeight() + BOUNDARY_SIZE );
                                                     }
                                                 }
        );

        final double px4 = rectangle.getX() + rectangle.getWidth();
        final double py4 = rectangle.getY() + rectangle.getHeight();
        controlPoint4 = new DefaultControlPoint( px4,
                                                 py4,
                                                 new ControlPointMoveHandler() {
                                                     @Override
                                                     public void onMove( double x,
                                                                         double y ) {
                                                         controlPoint2.setX( controlPoint4.getX() );
                                                         controlPoint3.setY( controlPoint4.getY() );
                                                         rectangle.setWidth( controlPoint2.getX() - controlPoint1.getX() );
                                                         rectangle.setHeight( controlPoint3.getY() - controlPoint1.getY() );
                                                         bounding.setWidth( rectangle.getWidth() + BOUNDARY_SIZE );
                                                         bounding.setHeight( rectangle.getHeight() + BOUNDARY_SIZE );
                                                     }
                                                 }
        );
        addControlPoint( controlPoint1 );
        addControlPoint( controlPoint2 );
        addControlPoint( controlPoint3 );
        addControlPoint( controlPoint4 );
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
        if ( _x < rectangle.getX() ) {
            return false;
        } else if ( _x > rectangle.getX() + rectangle.getWidth() ) {
            return false;
        } else if ( _y < rectangle.getY() ) {
            return false;
        } else if ( _y > rectangle.getY() + rectangle.getHeight() ) {
            return false;
        }
        return true;
    }

    @Override
    public void setHover( final boolean isHover ) {
        if ( isHover ) {
            rectangle.setFillColor( ShapesUtils.RGB_FILL_HOVER_CONTAINER );
            rectangle.setStrokeColor( ShapesUtils.RGB_STROKE_HOVER_CONTAINER );
        } else {
            rectangle.setFillColor( rectangleFillColour );
            rectangle.setStrokeColor( rectangleStokeColour );
        }
    }

    @Override
    public String toString() {
        return "WiresRectangularContainer{" + "id=" + getId() + ",x = " + getX() + ", y = " + getY() + "}";
    }

}
