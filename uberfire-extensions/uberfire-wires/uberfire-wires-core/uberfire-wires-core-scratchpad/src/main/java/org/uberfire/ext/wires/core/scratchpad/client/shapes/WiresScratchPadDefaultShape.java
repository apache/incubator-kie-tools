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
package org.uberfire.ext.wires.core.scratchpad.client.shapes;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import org.uberfire.ext.wires.core.api.containers.ContainerManager;
import org.uberfire.ext.wires.core.api.containers.RequiresContainerManager;
import org.uberfire.ext.wires.core.api.containers.WiresContainer;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseDynamicShape;

/**
 * A Shape that can be re-sized and have connectors attached. It CAN be added to Containers.
 */
public abstract class WiresScratchPadDefaultShape extends WiresBaseDynamicShape implements RequiresContainerManager {

    protected ContainerManager containerManager;

    private WiresContainer boundContainer;

    public WiresScratchPadDefaultShape() {
        //Check for the Shape being added to a Container as it is dragged around
        addNodeDragMoveHandler( new NodeDragMoveHandler() {

            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                boundContainer = containerManager.getContainer( WiresScratchPadDefaultShape.this.getX(),
                                                                WiresScratchPadDefaultShape.this.getY() );
                if ( boundContainer != null ) {
                    boundContainer.detachShape( WiresScratchPadDefaultShape.this );
                }

                getLayer().batch();
            }
        } );

        //When the drag ends; if it was within a Container add this Shape to the Container
        addNodeDragEndHandler( new NodeDragEndHandler() {

            @Override
            public void onNodeDragEnd( final NodeDragEndEvent nodeDragEndEvent ) {
                if ( boundContainer != null ) {
                    boundContainer.attachShape( WiresScratchPadDefaultShape.this );
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

}
