/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kie.uberfire.wires.core.api.shapes;

import java.util.ArrayList;
import java.util.List;

import com.emitrom.lienzo.client.core.event.NodeDragMoveEvent;
import com.emitrom.lienzo.client.core.event.NodeDragMoveHandler;
import com.emitrom.lienzo.client.core.shape.Group;
import com.emitrom.lienzo.client.core.shape.Layer;
import com.emitrom.lienzo.client.core.types.Point2D;
import org.kie.uberfire.wires.core.api.controlpoints.ControlPoint;
import org.kie.uberfire.wires.core.api.controlpoints.HasControlPoints;
import org.kie.uberfire.wires.core.api.magnets.HasMagnets;
import org.kie.uberfire.wires.core.api.magnets.Magnet;

/**
 * A Shape that can be re-sized and have connectors attached. It CANNOT be added to Containers.
 */
public abstract class WiresBaseDynamicShape extends WiresBaseShape implements HasMagnets,
                                                                              HasControlPoints {

    protected List<Magnet> magnets = new ArrayList<Magnet>();
    protected List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();

    private boolean showingMagnets = false;
    private boolean showingControlPoints = false;

    public WiresBaseDynamicShape() {
        //Update Magnets and ControlPoints when the Shape is dragged
        addNodeDragMoveHandler( new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                updateMagnetOffsets();
                updateControlPointOffsets();
                getLayer().draw();
            }
        } );
    }

    @Override
    public void addControlPoint( final ControlPoint cp ) {
        controlPoints.add( cp );
    }

    @Override
    public void showControlPoints() {
        final Layer layer = getLayer();
        if ( !controlPoints.isEmpty() && !showingControlPoints ) {
            for ( ControlPoint cp : controlPoints ) {
                cp.setOffset( getLocation() );
                layer.add( cp );
            }
            showingControlPoints = true;
            getLayer().draw();
        }
    }

    @Override
    public void hideControlPoints() {
        final Layer layer = getLayer();
        if ( !controlPoints.isEmpty() && showingControlPoints ) {
            for ( ControlPoint cp : controlPoints ) {
                layer.remove( cp );
            }
            showingControlPoints = false;
            getLayer().draw();
        }
    }

    @Override
    public void addMagnet( final Magnet m ) {
        magnets.add( m );
    }

    @Override
    public List<Magnet> getMagnets() {
        return magnets;
    }

    @Override
    public void showMagnetsPoints() {
        final Layer layer = getLayer();
        if ( !magnets.isEmpty() && !showingMagnets ) {
            for ( Magnet m : magnets ) {
                m.setOffset( getLocation() );
                layer.add( m );
            }
            showingMagnets = true;
            getLayer().draw();
        }
    }

    @Override
    public void hideMagnetPoints() {
        final Layer layer = getLayer();
        if ( !magnets.isEmpty() && showingMagnets ) {
            for ( Magnet m : magnets ) {
                layer.remove( m );
            }
            showingMagnets = false;
            getLayer().draw();
        }
    }

    @Override
    public void destroy() {
        hideControlPoints();
        hideMagnetPoints();
        super.destroy();
    }

    @Override
    public Group setX( final double x ) {
        final Group g = super.setX( x );
        updateMagnetOffsets();
        updateControlPointOffsets();
        return g;
    }

    @Override
    public Group setY( final double y ) {
        final Group g = super.setY( y );
        updateMagnetOffsets();
        updateControlPointOffsets();
        return g;
    }

    @Override
    public Group setLocation( final Point2D p ) {
        final Group g = super.setLocation( p );
        updateMagnetOffsets();
        updateControlPointOffsets();
        return g;
    }

    @Override
    public Group setOffset( final Point2D offset ) {
        final Group g = super.setOffset( offset );
        updateMagnetOffsets();
        updateControlPointOffsets();
        return g;
    }

    protected void updateMagnetOffsets() {
        if ( magnets == null ) {
            return;
        }
        for ( Magnet m : magnets ) {
            m.setOffset( getLocation() );
        }
    }

    protected void updateControlPointOffsets() {
        if ( controlPoints == null ) {
            return;
        }
        for ( ControlPoint cp : controlPoints ) {
            cp.setOffset( getLocation() );
        }
    }

}
