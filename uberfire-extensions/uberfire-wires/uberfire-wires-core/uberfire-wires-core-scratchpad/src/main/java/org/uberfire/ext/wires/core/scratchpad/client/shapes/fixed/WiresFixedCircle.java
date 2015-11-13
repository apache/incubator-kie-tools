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
package org.uberfire.ext.wires.core.scratchpad.client.shapes.fixed;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.shape.Circle;
import org.uberfire.ext.wires.core.api.containers.ContainerManager;
import org.uberfire.ext.wires.core.api.containers.RequiresContainerManager;
import org.uberfire.ext.wires.core.api.containers.WiresContainer;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;

public class WiresFixedCircle extends WiresBaseShape implements RequiresContainerManager {

    private static final int BOUNDARY_SIZE = 10;

    private final Circle circle;
    private final Circle bounding;

    private WiresContainer boundContainer;

    protected ContainerManager containerManager;

    public WiresFixedCircle( final Circle shape ) {
        circle = shape;

        bounding = new Circle( circle.getRadius() + ( BOUNDARY_SIZE / 2 ) );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        add( circle );

        //This class doesn't extend a super-class that handles Containers, so we add it manually
        //Check for the Shape being added to a Container as it is dragged around
        addNodeDragMoveHandler( new NodeDragMoveHandler() {

            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                boundContainer = containerManager.getContainer( WiresFixedCircle.this.getX(),
                                                                WiresFixedCircle.this.getY() );
                if ( boundContainer != null ) {
                    boundContainer.detachShape( WiresFixedCircle.this );
                }

                getLayer().batch();
            }
        } );

        //When the drag ends; if it was within a Container add this Shape to the Container
        addNodeDragEndHandler( new NodeDragEndHandler() {

            @Override
            public void onNodeDragEnd( final NodeDragEndEvent nodeDragEndEvent ) {
                if ( boundContainer != null ) {
                    boundContainer.attachShape( WiresFixedCircle.this );
                    boundContainer.setHover( false );
                }

                getLayer().batch();
            }
        } );
    }

    @Override
    public void setContainerManager( final ContainerManager containerManager ) {
        this.containerManager = containerManager;
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
        return false;
    }
}
