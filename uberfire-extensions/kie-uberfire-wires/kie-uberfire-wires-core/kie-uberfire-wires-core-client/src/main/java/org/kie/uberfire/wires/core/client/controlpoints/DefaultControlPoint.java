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
package org.kie.uberfire.wires.core.client.controlpoints;

import com.emitrom.lienzo.client.core.event.NodeDragMoveEvent;
import com.emitrom.lienzo.client.core.event.NodeDragMoveHandler;
import com.emitrom.lienzo.client.core.shape.Circle;
import org.kie.uberfire.wires.core.api.controlpoints.ControlPoint;
import org.kie.uberfire.wires.core.api.controlpoints.ControlPointMoveHandler;
import org.kie.uberfire.wires.core.api.shapes.UUID;

import static org.kie.uberfire.wires.core.client.util.ShapesUtils.*;

/**
 * Default ControlPoint that informs the registered handler of changes to state
 */
public class DefaultControlPoint extends Circle implements ControlPoint<Circle> {

    private static final int RADIUS = 8;

    private final String id;
    private final ControlPointMoveHandler cpMoveHandler;

    public DefaultControlPoint( final double x,
                                final double y,
                                final ControlPointMoveHandler cpMoveHandler ) {
        super( RADIUS );
        this.id = UUID.uuid();
        this.cpMoveHandler = cpMoveHandler;

        setFillColor( CP_RGB_FILL_COLOR );
        setStrokeWidth( CP_RGB_STROKE_WIDTH_SHAPE );
        setX( x );
        setY( y );
        setDraggable( true );

        setupHandlers( cpMoveHandler );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ControlPointMoveHandler getHandler() {
        return cpMoveHandler;
    }

    protected void setupHandlers( final ControlPointMoveHandler moveHandler ) {
        addNodeDragMoveHandler( new NodeDragMoveHandler() {

            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                moveHandler.onMove( DefaultControlPoint.this.getX(),
                                    DefaultControlPoint.this.getY() );
                getLayer().draw();
            }
        } );
    }

    @Override
    public String toString() {
        return "DefaultControlPoint{" + "id=" + id + "}";
    }

}
