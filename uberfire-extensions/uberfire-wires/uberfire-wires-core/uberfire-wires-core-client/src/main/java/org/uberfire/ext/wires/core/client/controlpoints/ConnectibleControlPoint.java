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
package org.uberfire.ext.wires.core.client.controlpoints;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import org.uberfire.ext.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.uberfire.ext.wires.core.api.controlpoints.HasControlPoints;
import org.uberfire.ext.wires.core.api.magnets.Magnet;
import org.uberfire.ext.wires.core.api.magnets.MagnetManager;

/**
 * A Control Point that can be connected to Magnets
 */
public class ConnectibleControlPoint extends DefaultControlPoint {

    private static final int MAGNET_ATTRACTION = 30;

    private Magnet boundMagnet;

    private final HasControlPoints shape;
    private final MagnetManager magnetManager;

    public ConnectibleControlPoint( final double x,
                                    final double y,
                                    final HasControlPoints shape,
                                    final MagnetManager magnetManager,
                                    final ControlPointMoveHandler cpMoveHandler ) {
        super( x,
               y,
               cpMoveHandler );
        this.shape = shape;
        this.magnetManager = magnetManager;
    }

    @Override
    protected void setupHandlers( final ControlPointMoveHandler handler ) {
        addNodeDragMoveHandler( new NodeDragMoveHandler() {

            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                handler.onMove( ConnectibleControlPoint.this.getX(),
                                ConnectibleControlPoint.this.getY() );

                if ( boundMagnet != null ) {
                    boundMagnet.detachControlPoint( ConnectibleControlPoint.this );
                }

                boundMagnet = magnetManager.getMagnet( shape,
                                                       ConnectibleControlPoint.this.getX(),
                                                       ConnectibleControlPoint.this.getY() );

                getLayer().batch();
            }
        } );

        addNodeDragEndHandler( new NodeDragEndHandler() {

            @Override
            public void onNodeDragEnd( final NodeDragEndEvent nodeDragEndEvent ) {
                if ( boundMagnet != null ) {
                    double deltaX = getX() - boundMagnet.getX();
                    double deltaY = getY() - boundMagnet.getY();
                    double distance = Math.sqrt( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) );
                    if ( distance < MAGNET_ATTRACTION ) {
                        boundMagnet.attachControlPoint( ConnectibleControlPoint.this );
                        final double x = boundMagnet.getX();
                        final double y = boundMagnet.getY();
                        setX( x );
                        setY( y );
                        handler.onMove( x,
                                        y );
                    }
                }
                magnetManager.hideAllMagnets();

                getLayer().batch();
            }
        } );
    }

    public Magnet getBoundMagnet() {
        return boundMagnet;
    }

    @Override
    public String toString() {
        return "ConnectibleControlPoint{" + "id=" + getId() + "}";
    }

}
