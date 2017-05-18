/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.components.drag;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.util.EdgeMagnetsHelper;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
public class NodeDragProxyImpl implements NodeDragProxy<AbstractCanvasHandler> {

    private AbstractCanvasHandler canvasHandler;

    ShapeDragProxy<AbstractCanvas> shapeDragProxyFactory;
    EdgeMagnetsHelper magnetsHelper;

    private EdgeShape transientEdgeShape;

    @Inject
    public NodeDragProxyImpl(final ShapeDragProxy<AbstractCanvas> shapeDragProxyFactory,
                             final EdgeMagnetsHelper magnetsHelper) {
        this.shapeDragProxyFactory = shapeDragProxyFactory;
        this.magnetsHelper = magnetsHelper;
    }

    @Override
    public DragProxy<AbstractCanvasHandler, Item, NodeDragProxyCallback> proxyFor(final AbstractCanvasHandler context) {
        this.canvasHandler = context;
        this.shapeDragProxyFactory.proxyFor(context.getAbstractCanvas());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DragProxy<AbstractCanvasHandler, Item, NodeDragProxyCallback> show(final Item item,
                                                                              final int x,
                                                                              final int y,
                                                                              final NodeDragProxyCallback callback) {
        final AbstractCanvas canvas = canvasHandler.getAbstractCanvas();
        final Node<View<?>, Edge> node = item.getNode();
        final ShapeFactory<Object, AbstractCanvasHandler, ?> nodeShapeFactory = item.getNodeShapeFactory();
        final Edge<View<?>, Node> inEdge = item.getInEdge();
        final Node<View<?>, Edge> inEdgeSourceNode = item.getInEdgeSourceNode();
        final ShapeFactory<Object, AbstractCanvasHandler, ?> edgeShapeFactory = item.getInEdgeShapeFactory();
        final Shape nodeShape = nodeShapeFactory.build(node.getContent().getDefinition(),
                                                       canvasHandler);
        if (nodeShape instanceof ElementShape) {
            ((ElementShape) nodeShape).applyProperties(node,
                                                       MutationContext.STATIC);
        }
        this.transientEdgeShape = (EdgeShape) edgeShapeFactory.build(inEdge.getContent().getDefinition(),
                                                                     canvasHandler);
        canvas.addTransientShape(this.transientEdgeShape);
        this.transientEdgeShape.applyProperties(inEdge,
                                                MutationContext.STATIC);
        final Shape<?> edgeSourceNodeShape = canvasHandler.getCanvas().getShape(inEdgeSourceNode.getUUID());
        shapeDragProxyFactory.show(nodeShape,
                                   x,
                                   y,
                                   new DragProxyCallback() {

                                       @Override
                                       public void onStart(final int x,
                                                           final int y) {
                                           callback.onStart(x,
                                                            y);
                                           drawEdge();
                                       }

                                       @Override
                                       public void onMove(final int x,
                                                          final int y) {
                                           callback.onMove(x,
                                                           y);
                                           drawEdge();
                                       }

                                       @Override
                                       public void onComplete(final int x,
                                                              final int y) {
                                           final Magnet[] magnets = getMagnets();
                                           callback.onComplete(x,
                                                               y);
                                           callback.onComplete(x,
                                                               y,
                                                               magnets[0],
                                                               magnets[1]);
                                           deleteTransientEdgeShape();
                                           canvas.draw();
                                       }

                                       private void drawEdge() {
                                           if (inEdge.getContent() instanceof ViewConnector) {
                                               final Magnet[] magnets = getMagnets();
                                               final ViewConnector viewConnector = (ViewConnector) inEdge.getContent();
                                               viewConnector.setSourceMagnet(magnets[0]);
                                               viewConnector.setTargetMagnet(magnets[1]);
                                           }
                                           NodeDragProxyImpl.this.transientEdgeShape.applyConnections(inEdge,
                                                                                                      edgeSourceNodeShape.getShapeView(),
                                                                                                      nodeShape.getShapeView(),
                                                                                                      MutationContext.STATIC);
                                           canvas.draw();
                                       }

                                       private Magnet[] getMagnets() {
                                           return magnetsHelper.getDefaultMagnets(edgeSourceNodeShape.getShapeView(),
                                                                                       nodeShape.getShapeView());
                                       }
                                   });
        return this;
    }

    @Override
    public void clear() {
        if (null != shapeDragProxyFactory) {
            this.shapeDragProxyFactory.clear();
        }
        deleteTransientEdgeShape();
    }

    @Override
    public void destroy() {
        if (null != shapeDragProxyFactory) {
            clear();
            this.shapeDragProxyFactory.destroy();
        }
        this.shapeDragProxyFactory = null;
        this.canvasHandler = null;
        this.magnetsHelper = null;
        this.transientEdgeShape = null;
    }

    private AbstractCanvas getCanvas() {
        return canvasHandler.getAbstractCanvas();
    }

    private void deleteTransientEdgeShape() {
        if (null != this.transientEdgeShape) {
            getCanvas().deleteTransientShape(this.transientEdgeShape);
            getCanvas().draw();
            this.transientEdgeShape = null;
        }
    }
}
