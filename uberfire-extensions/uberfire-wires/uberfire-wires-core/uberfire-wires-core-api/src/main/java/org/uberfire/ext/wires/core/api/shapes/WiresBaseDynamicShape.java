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
package org.uberfire.ext.wires.core.api.shapes;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPoint;
import org.uberfire.ext.wires.core.api.controlpoints.HasControlPoints;
import org.uberfire.ext.wires.core.api.magnets.HasMagnets;
import org.uberfire.ext.wires.core.api.magnets.Magnet;

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
                updateMagnetLocations( 0, 0 );
                updateControlPointLocations( 0, 0 );
                getLayer().batch();
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
                layer.add( cp );
            }
            showingControlPoints = true;
            getLayer().batch();
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
            getLayer().batch();
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
                layer.add( m );
            }
            showingMagnets = true;
            getLayer().batch();
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
            getLayer().batch();
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
        final double dx = x - getX();
        final double dy = 0;
        updateMagnetLocations( dx,
                               dy );
        updateControlPointLocations( dx,
                                     dy );
        final Group g = super.setX( x );
        return g;
    }

    @Override
    public Group setY( final double y ) {
        final double dx = 0;
        final double dy = y - getY();
        updateMagnetLocations( dx,
                               dy );
        updateControlPointLocations( dx,
                                     dy );
        final Group g = super.setY( y );
        return g;
    }

    protected void updateMagnetLocations( final double dx,
                                          final double dy ) {
        if ( magnets == null ) {
            return;
        }
        for ( Magnet m : magnets ) {
            m.move( dx,
                    dy );
        }
    }

    protected void updateControlPointLocations( final double dx,
                                                final double dy ) {
        if ( controlPoints == null ) {
            return;
        }
        for ( ControlPoint cp : controlPoints ) {
            cp.move( dx,
                     dy );
        }
    }

}
