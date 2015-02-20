/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uberfire.ext.wires.core.api.shapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.wires.core.api.containers.WiresContainer;

/**
 * A Container that can be re-sized and have connectors attached.
 */
public abstract class WiresBaseDynamicContainer extends WiresBaseDynamicShape implements WiresContainer,
                                                                                         RequiresShapesManager {

    private List<WiresBaseShape> children = new ArrayList<WiresBaseShape>();
    private List<Pair<WiresBaseShape, Point2D>> dragStartLocations = new ArrayList<Pair<WiresBaseShape, Point2D>>();

    protected ShapesManager shapesManager;

    public WiresBaseDynamicContainer() {
        //Record the start location of Children when the Container is dragged. These are
        //used to calculate the new positions of Children as the Container is dragged
        addNodeDragStartHandler( new NodeDragStartHandler() {
            @Override
            public void onNodeDragStart( final NodeDragStartEvent nodeDragStartEvent ) {
                dragStartLocations.clear();
                for ( WiresBaseShape shape : children ) {
                    dragStartLocations.add( new Pair<WiresBaseShape, Point2D>( shape,
                                                                               new Point2D( shape.getLocation().getX(),
                                                                                            shape.getLocation().getY() ) ) );
                }
            }
        } );

        //As the Container is dragged update the location of the Children,
        //using their start location and the DragContext DX, DY
        addNodeDragMoveHandler( new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                final double deltaX = nodeDragMoveEvent.getDragContext().getDx();
                final double deltaY = nodeDragMoveEvent.getDragContext().getDy();
                final Point2D delta = new Point2D( deltaX,
                                                   deltaY );
                for ( Pair<WiresBaseShape, Point2D> dragStartLocation : dragStartLocations ) {
                    dragStartLocation.getK1().setLocation( dragStartLocation.getK2().add( delta ) );
                }
                getLayer().draw();
            }
        } );

    }

    @Override
    public void setShapesManager( final ShapesManager shapesManager ) {
        this.shapesManager = shapesManager;
    }

    @Override
    public void attachShape( final WiresBaseShape shape ) {
        children.add( shape );
    }

    @Override
    public void detachShape( final WiresBaseShape shape ) {
        children.remove( shape );
    }

    @Override
    public List<WiresBaseShape> getContainedShapes() {
        return Collections.unmodifiableList( children );
    }

    @Override
    public Group setX( final double x ) {
        updateChildrenLocations( x - getX(),
                                 0 );
        return super.setX( x );
    }

    @Override
    public Group setY( final double y ) {
        updateChildrenLocations( 0,
                                 y - getY() );
        return super.setY( y );
    }

    protected void updateChildrenLocations( final double deltaX,
                                            final double deltaY ) {
        if ( children == null ) {
            return;
        }
        final Point2D delta = new Point2D( deltaX,
                                           deltaY );
        for ( WiresBaseShape shape : children ) {
            shape.setLocation( shape.getLocation().add( delta ) );
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        for ( WiresBaseShape shape : children ) {
            shapesManager.forceDeleteShape( shape );
        }
    }

}
