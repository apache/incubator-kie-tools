/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kie.uberfire.wires.core.client.magnets;

import java.util.ArrayList;
import java.util.List;

import com.emitrom.lienzo.client.core.shape.Circle;
import com.emitrom.lienzo.client.core.types.Point2D;
import org.kie.uberfire.wires.core.api.controlpoints.ControlPoint;
import org.kie.uberfire.wires.core.api.magnets.Magnet;
import org.kie.uberfire.wires.core.api.shapes.UUID;

import static org.kie.uberfire.wires.core.client.util.ShapesUtils.*;

public class DefaultMagnet extends Circle implements Magnet<Circle> {

    private static final int RADIUS = 8;

    private final String id;
    private final List<ControlPoint> attachedControlPoints = new ArrayList<ControlPoint>();

    public DefaultMagnet( final double x,
                          final double y ) {
        super( RADIUS );
        this.id = UUID.uuid();

        setFillColor( MAGNET_RGB_FILL_SHAPE );
        setStrokeWidth( CP_RGB_STROKE_WIDTH_SHAPE );
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
            setFillColor( MAGNET_ACTIVE_RGB_FILL_SHAPE );
        } else {
            setFillColor( MAGNET_RGB_FILL_SHAPE );
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
    public Circle setOffset( final Point2D offset ) {
        final Circle c = super.setOffset( offset );
        moveAttachedControlPoints();
        return c;
    }

    protected void moveAttachedControlPoints() {
        final List<ControlPoint> controlPoints = getAttachedControlPoints();
        if ( controlPoints == null || controlPoints.isEmpty() ) {
            return;
        }
        for ( ControlPoint cp : controlPoints ) {
            final double dx = getX() + getOffset().getX() - cp.getOffset().getX();
            final double dy = getY() + getOffset().getY() - cp.getOffset().getY();
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
