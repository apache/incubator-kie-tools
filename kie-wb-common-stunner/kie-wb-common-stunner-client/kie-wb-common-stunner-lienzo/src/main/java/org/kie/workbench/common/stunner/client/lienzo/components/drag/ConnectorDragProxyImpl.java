/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.drag;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
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
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

@Dependent
public class ConnectorDragProxyImpl implements ConnectorDragProxy<AbstractCanvasHandler> {

    private final ShapeViewDragProxy<AbstractCanvas> shapeViewDragProxyFactory;
    private final GraphBoundsIndexer graphBoundsIndexer;
    private AbstractCanvasHandler canvasHandler;
    private WiresConnectorView<?> connectorShapeView;

    @Inject
    public ConnectorDragProxyImpl(final ShapeViewDragProxy<AbstractCanvas> shapeViewDragProxyFactory,
                                  final GraphBoundsIndexer graphBoundsIndexer) {
        this.shapeViewDragProxyFactory = shapeViewDragProxyFactory;
        this.graphBoundsIndexer = graphBoundsIndexer;
    }

    @Override
    public DragProxy<AbstractCanvasHandler, Item, DragProxyCallback> proxyFor(final AbstractCanvasHandler context) {
        this.canvasHandler = context;
        this.shapeViewDragProxyFactory.proxyFor(context.getAbstractCanvas());
        this.graphBoundsIndexer.setRootUUID(context.getDiagram().getMetadata().getCanvasRootUUID());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DragProxy<AbstractCanvasHandler, Item, DragProxyCallback> show(final Item item,
                                                                          final int x,
                                                                          final int y,
                                                                          final DragProxyCallback callback) {
        clear();
        // Source connector's shape - Obtain the shape for the source node.
        final Node<View<?>, Edge> sourceNode = item.getSourceNode();
        final Shape<?> sourceNodeShape = getCanvas().getShape(sourceNode.getUUID());

        // Target connector's shape - Create a temporary shape view, that will act as the connector's target node.
        final WiresShapeView transientShapeView =
                new WiresShapeView<>(new MultiPath().rect(0,
                                                          0,
                                                          1,
                                                          1)
                                             .setFillAlpha(0)
                                             .setStrokeAlpha(0));
        getWiresManager().getMagnetManager().createMagnets(transientShapeView);

        // Create the transient connector's shape and view.
        final Edge<View<?>, Node> edge = item.getEdge();
        final Shape<?> edgeShape =
                ((ShapeFactory<Object, ?>) item.getShapeFactory())
                        .newShape(edge.getContent().getDefinition());
        final EdgeShape connectorShape = (EdgeShape) edgeShape;
        this.connectorShapeView = (WiresConnectorView<?>) edgeShape.getShapeView();

        // Register and update shape's view as for edge bean's state.
        getWiresManager().register(connectorShapeView);
        connectorShape.applyProperties(edge,
                                       MutationContext.STATIC);

        // Apply connector's connections for both source and target shapes.
        // Using center connector strategy, so magnet index 0.
        final MagnetConnection centerConnection =
                new MagnetConnection.Builder()
                        .atX(0)
                        .atY(0)
                        .magnet(0)
                        .build();
        connectorShapeView.connect(sourceNodeShape.getShapeView(),
                                   centerConnection,
                                   transientShapeView,
                                   centerConnection);

        // Optimize the index and show the drag proxy for the temporary shape view.
        graphBoundsIndexer.build(canvasHandler.getDiagram().getGraph());
        shapeViewDragProxyFactory.show(transientShapeView,
                                       x,
                                       y,
                                       new DragProxyCallback() {

                                           @Override
                                           public void onStart(final int x,
                                                               final int y) {
                                               callback.onStart(x,
                                                                y);
                                               // As using center magnet, update the connection.
                                               connectorShapeView.updateForCenterConnection();
                                           }

                                           @Override
                                           public void onMove(final int x,
                                                              final int y) {
                                               callback.onMove(x,
                                                               y);
                                               // As using center magnet, update the connection.
                                               connectorShapeView.updateForCenterConnection();
                                           }

                                           @Override
                                           public void onComplete(final int x,
                                                                  final int y) {
                                               callback.onComplete(x,
                                                                   y);
                                               deregisterTransientConnector();
                                               getCanvas().draw();
                                           }
                                       });
        return this;
    }

    @Override
    public void clear() {
        shapeViewDragProxyFactory.clear();
        deregisterTransientConnector();
    }

    public void destroy() {
        deregisterTransientConnector();
        shapeViewDragProxyFactory.destroy();
        graphBoundsIndexer.destroy();
        canvasHandler = null;
    }

    private WiresManager getWiresManager() {
        final AbstractCanvas<?> canvas = canvasHandler.getAbstractCanvas();
        final LienzoLayer layer = (LienzoLayer) canvas.getLayer();
        return WiresManager.get(layer.getLienzoLayer());
    }

    private void deregisterTransientConnector() {
        if (null != this.connectorShapeView) {
            getWiresManager().deregister(connectorShapeView);
            getCanvas().draw();
            this.connectorShapeView = null;
        }
    }

    private AbstractCanvas<?> getCanvas() {
        return canvasHandler.getAbstractCanvas();
    }
}
