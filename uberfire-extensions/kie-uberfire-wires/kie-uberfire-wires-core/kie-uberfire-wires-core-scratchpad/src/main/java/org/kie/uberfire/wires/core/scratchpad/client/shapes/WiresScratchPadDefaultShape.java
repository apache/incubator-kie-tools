/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kie.uberfire.wires.core.scratchpad.client.shapes;

import com.emitrom.lienzo.client.core.event.NodeDragEndEvent;
import com.emitrom.lienzo.client.core.event.NodeDragEndHandler;
import com.emitrom.lienzo.client.core.event.NodeDragMoveEvent;
import com.emitrom.lienzo.client.core.event.NodeDragMoveHandler;
import org.kie.uberfire.wires.core.api.containers.ContainerManager;
import org.kie.uberfire.wires.core.api.containers.RequiresContainerManager;
import org.kie.uberfire.wires.core.api.containers.WiresContainer;
import org.kie.uberfire.wires.core.api.shapes.WiresBaseDynamicShape;

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

                getLayer().draw();
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

                getLayer().draw();
            }
        } );
    }

    @Override
    public void setContainerManager( final ContainerManager containerManager ) {
        this.containerManager = containerManager;
    }

}
