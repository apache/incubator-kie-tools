/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.components.proxies;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.NodeShape;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.event.AbstractMouseEvent;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
public class NodeProxy implements ShapeProxy {

    private final ElementProxy proxy;
    private final ShapeProxyView<NodeShape> view;

    private Node<View<?>, Edge> targetNode;
    private Edge<ViewConnector<?>, Node> edge;
    private Node<View<?>, Edge> sourceNode;

    private SessionManager sessionManager;

    @Inject
    public NodeProxy(final ElementProxy proxy,
                     final ShapeProxyView<NodeShape> view) {
        this.proxy = proxy;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        proxy
                .setView(view)
                .setProxyBuilder(this::onCreateProxy)
                .handleCancelKey();
    }

    public NodeProxy setCanvasHandler(final AbstractCanvasHandler canvasHandler) {
        proxy.setCanvasHandler(canvasHandler);
        return this;
    }

    public NodeProxy setTargetNode(Node<View<?>, Edge> targetNode) {
        this.targetNode = targetNode;
        return this;
    }

    public NodeProxy setEdge(Edge<ViewConnector<?>, Node> edge) {
        this.edge = edge;
        return this;
    }

    public NodeProxy setSourceNode(Node<View<?>, Edge> sourceNode) {
        this.sourceNode = sourceNode;
        return this;
    }

    public NodeProxy start(final AbstractMouseEvent event) {
        start(event.getX(), event.getY());
        return this;
    }

    @Override
    public void start(final double x,
                      final double y) {
        proxy.start(x, y);
    }

    @Override
    public void destroy() {
        proxy.destroy();
        targetNode = null;
        edge = null;
        sourceNode = null;
    }

    private NodeShape onCreateProxy() {
        final CanvasCommandFactory<AbstractCanvasHandler> commandFactory = proxy.lookupCanvasFactory();
        final Node<View<?>, Edge> parent = getParent();
        proxy.execute(new DeferredCompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                              .deferCommand(() -> null != parent ?
                                      commandFactory.addChildNode(parent,
                                                                  targetNode,
                                                                  getShapeSetId()) :
                                      commandFactory.addNode(targetNode,
                                                             getShapeSetId()))
                              .deferCommand(() -> commandFactory.addConnector(sourceNode,
                                                                              edge,
                                                                              MagnetConnection.Builder.atCenter(sourceNode),
                                                                              getShapeSetId()))
                              .deferCommand(() -> commandFactory.setTargetNode(targetNode,
                                                                               edge,
                                                                               MagnetConnection.Builder.forTarget(sourceNode,
                                                                                                                  targetNode)))
                              .build());
        final Canvas canvas = proxy.getCanvas();
        final NodeShape targetShape = getTargetShape();
        final Shape<?> edgeShape = canvas.getShape(edge.getUUID());
        edgeShape.applyState(ShapeState.SELECTED);
        return targetShape;
    }

    private Node<View<?>, Edge> getParent() {
        return (Node<View<?>, Edge>) GraphUtils.getParent(sourceNode);
    }

    private NodeShape getTargetShape() {
        return (NodeShape) proxy.getCanvas().getShape(targetNode.getUUID());
    }

    private Metadata getMetadata() {
        return proxy.getCanvasHandler().getDiagram().getMetadata();
    }

    private String getShapeSetId() {
        return getMetadata().getShapeSetId();
    }
}
