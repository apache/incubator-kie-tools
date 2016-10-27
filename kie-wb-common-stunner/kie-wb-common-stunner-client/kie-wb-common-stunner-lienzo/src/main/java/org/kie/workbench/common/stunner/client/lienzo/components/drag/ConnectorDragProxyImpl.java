/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.drag;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.drag.ConnectorDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.drag.ShapeViewDragProxy;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ConnectorDragProxyImpl implements ConnectorDragProxy<AbstractCanvasHandler> {

    ShapeViewDragProxy<AbstractCanvas> shapeViewDragProxyFactory;
    GraphBoundsIndexer graphBoundsIndexer;

    private AbstractCanvasHandler canvasHandler;
    private WiresConnector wiresConnector;

    @Inject
    public ConnectorDragProxyImpl( final ShapeViewDragProxy<AbstractCanvas> shapeViewDragProxyFactory,
                                   final GraphBoundsIndexer graphBoundsIndexer ) {
        this.shapeViewDragProxyFactory = shapeViewDragProxyFactory;
        this.graphBoundsIndexer = graphBoundsIndexer;
    }

    @Override
    public DragProxy<AbstractCanvasHandler, Item, DragProxyCallback> proxyFor( final AbstractCanvasHandler context ) {
        this.canvasHandler = context;
        this.shapeViewDragProxyFactory.proxyFor( context.getCanvas() );
        this.graphBoundsIndexer.setRootUUID( context.getDiagram().getMetadata().getCanvasRootUUID() );
        return this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public DragProxy<AbstractCanvasHandler, Item, DragProxyCallback> show( Item item, int x, int y, DragProxyCallback callback ) {
        final Edge<View<?>, Node> edge = item.getEdge();
        final Node<View<?>, Edge> sourceNode = item.getSourceNode();
        final ShapeFactory<Object, AbstractCanvasHandler, ?> factory =
                ( ShapeFactory<Object, AbstractCanvasHandler, ?> ) item.getShapeFactory();
        final WiresManager wiresManager = getWiresManager();
        final Shape<?> sourceNodeShape = getCanvas().getShape( sourceNode.getUUID() );
        final Shape<?> shape = factory.build( edge.getContent().getDefinition(), canvasHandler );
        final EdgeShape connector = ( EdgeShape ) shape;
        this.wiresConnector = ( WiresConnector ) shape.getShapeView();
        wiresManager.register( wiresConnector );
        final MultiPath dummyPath = new MultiPath().rect( 0, 0, 1, 1 ).setFillAlpha( 0 ).setStrokeAlpha( 0 );
        final DummyShapeView dummyShapeView = new DummyShapeView( dummyPath );
        graphBoundsIndexer.build( canvasHandler.getDiagram().getGraph() );
        shapeViewDragProxyFactory.show( dummyShapeView, x, y, new DragProxyCallback() {

            @Override
            public void onStart( final int x,
                                 final int y ) {
                callback.onStart( x, y );
                drawConnector();

            }

            @Override
            public void onMove( final int x,
                                final int y ) {
                callback.onMove( x, y );
                drawConnector();

            }

            @Override
            public void onComplete( final int x,
                                    final int y ) {
                callback.onComplete( x, y );
                deregisterTransientConnector();
                getCanvas().draw();

            }

            private void drawConnector() {
                ShapeView<?> targetShapeView = null;
                // TODO: Apply target connection to mouse pointer, in adittion check if allowed connection to node at current pos is ok -> automatically connect to it
                // final Node targetNode = graphBoundsIndexer.getAt( x, y );
                final Node targetNode = null;
                if ( null != targetNode ) {
                    final Shape<?> targetNodeShape = getCanvas().getShape( targetNode.getUUID() );
                    if ( null != targetNodeShape ) {
                        targetShapeView = targetNodeShape.getShapeView();
                    }

                } else {
                    targetShapeView = dummyShapeView;
                }
                connector.applyConnections( edge, sourceNodeShape.getShapeView(), targetShapeView, MutationContext.STATIC );
                connector.applyProperties( edge, MutationContext.STATIC );
                getCanvas().draw();
            }

        } );
        return this;
    }

    @Override
    public void clear() {
        if ( null != this.shapeViewDragProxyFactory ) {
            this.shapeViewDragProxyFactory.clear();
        }
        deregisterTransientConnector();
    }

    public void destroy() {
        clear();
        this.graphBoundsIndexer.destroy();
        this.graphBoundsIndexer = null;
        this.canvasHandler = null;
        this.shapeViewDragProxyFactory.destroy();
        this.shapeViewDragProxyFactory = null;

    }

    private WiresManager getWiresManager() {
        final AbstractCanvas<?> canvas = canvasHandler.getCanvas();
        final LienzoLayer layer = ( LienzoLayer ) canvas.getLayer();
        return WiresManager.get( layer.getLienzoLayer() );
    }

    private void deregisterTransientConnector() {
        if ( null != this.wiresConnector ) {
            getWiresManager().deregister( wiresConnector );
            getCanvas().draw();
            this.wiresConnector = null;
        }
    }

    private AbstractCanvas<?> getCanvas() {
        return canvasHandler.getCanvas();
    }

    private class DummyShapeView extends WiresShape implements ShapeView<DummyShapeView> {

        public DummyShapeView( MultiPath path ) {
            super( path );
        }

        @Override
        public DummyShapeView setUUID( String uuid ) {
            return null;
        }

        @Override
        public String getUUID() {
            return null;
        }

        @Override
        public double getShapeX() {
            return 0;
        }

        @Override
        public double getShapeY() {
            return 0;
        }

        @Override
        public DummyShapeView setShapeX( double x ) {
            return null;
        }

        @Override
        public DummyShapeView setShapeY( double y ) {
            return null;
        }

        @Override
        public String getFillColor() {
            return null;
        }

        @Override
        public DummyShapeView setFillColor( String color ) {
            return null;
        }

        @Override
        public double getFillAlpha() {
            return 0;
        }

        @Override
        public DummyShapeView setFillAlpha( double alpha ) {
            return null;
        }

        @Override
        public String getStrokeColor() {
            return null;
        }

        @Override
        public DummyShapeView setStrokeColor( String color ) {
            return null;
        }

        @Override
        public double getStrokeAlpha() {
            return 0;
        }

        @Override
        public DummyShapeView setStrokeAlpha( double alpha ) {
            return null;
        }

        @Override
        public double getStrokeWidth() {
            return 0;
        }

        @Override
        public DummyShapeView setStrokeWidth( double width ) {
            return null;
        }

        @Override
        public DummyShapeView setDragEnabled( boolean isDraggable ) {
            return null;
        }

        @Override
        public DummyShapeView moveToTop() {
            return null;
        }

        @Override
        public DummyShapeView moveToBottom() {
            return null;
        }

        @Override
        public DummyShapeView moveUp() {
            return null;
        }

        @Override
        public DummyShapeView moveDown() {
            return null;
        }

        @Override
        public DummyShapeView setZIndex( int zindez ) {
            return null;
        }

        @Override
        public int getZIndex() {
            return 0;
        }

        @Override
        public void destroy() {
        }
    }
}
