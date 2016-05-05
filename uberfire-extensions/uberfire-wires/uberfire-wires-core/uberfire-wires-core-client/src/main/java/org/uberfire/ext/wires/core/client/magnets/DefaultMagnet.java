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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uberfire.ext.wires.core.client.magnets;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPoint;
import org.uberfire.ext.wires.core.api.magnets.Magnet;
import org.uberfire.ext.wires.core.api.shapes.UUID;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

public class DefaultMagnet extends Circle implements Magnet<Circle> {

    private static final int RADIUS = 8;

    private final String id;
    private final List<ControlPoint> attachedControlPoints = new ArrayList<ControlPoint>();

    public DefaultMagnet( final double x,
                          final double y ) {
        super( RADIUS );
        this.id = UUID.uuid();

        setFillColor( ShapesUtils.MAGNET_RGB_FILL_SHAPE );
        setStrokeWidth( ShapesUtils.CP_RGB_STROKE_WIDTH_SHAPE );
        setX( x );
        setY( y );
        setDraggable( false );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void attachControlPoint( final ControlPoint controlPoint ) {
        attachedControlPoints.add( controlPoint );
    }

    @Override
    public void detachControlPoint( final ControlPoint controlPoint ) {
        attachedControlPoints.remove( controlPoint );
    }

    @Override
    public List<ControlPoint> getAttachedControlPoints() {
        return attachedControlPoints;
    }

    @Override
    public void setActive( final boolean isActive ) {
        if ( isActive ) {
            setFillColor( ShapesUtils.MAGNET_ACTIVE_RGB_FILL_SHAPE );
        } else {
            setFillColor( ShapesUtils.MAGNET_RGB_FILL_SHAPE );
        }
    }

    @Override
    public Circle setX( final double x ) {
        final Circle c = super.setX( x );
        moveAttachedControlPoints();
        return c;
    }

    @Override
    public Circle setY( final double y ) {
        final Circle c = super.setY( y );
        moveAttachedControlPoints();
        return c;
    }

    @Override
    public Circle setLocation( final Point2D p ) {
        final Circle c = super.setLocation( p );
        moveAttachedControlPoints();
        return c;
    }

    @Override
    public void move( final double dx,
                      final double dy ) {
        setLocation( getLocation().add( new Point2D( dx,
                                                     dy ) ) );
    }

    protected void moveAttachedControlPoints() {
        final List<ControlPoint> controlPoints = getAttachedControlPoints();
        if ( controlPoints == null || controlPoints.isEmpty() ) {
            return;
        }
        for ( ControlPoint cp : controlPoints ) {
            final double dx = getX();
            final double dy = getY();
            cp.setX( dx );
            cp.setY( dy );
            cp.getHandler().onMove( dx,
                                    dy );
        }
    }

    @Override
    public String toString() {
        return "DefaultMagnet{" + "id=" + id + '}';
    }

}
