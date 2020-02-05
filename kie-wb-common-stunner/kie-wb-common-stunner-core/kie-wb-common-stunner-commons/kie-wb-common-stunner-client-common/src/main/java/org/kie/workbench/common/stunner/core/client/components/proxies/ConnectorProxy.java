/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.proxies;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.view.event.AbstractMouseEvent;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
public class ConnectorProxy implements ShapeProxy {

    private final ElementProxy proxy;
    private final ShapeProxyView<EdgeShape> view;

    private Edge<? extends ViewConnector<?>, Node> edge;
    private Node<? extends View<?>, Edge> sourceNode;

    @Inject
    public ConnectorProxy(final ElementProxy proxy,
                          final ShapeProxyView<EdgeShape> view) {
        this.proxy = proxy;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        proxy
                .setView(view)
                .setProxyBuilder(this::onCreateProxy);
    }

    public ConnectorProxy setCanvasHandler(final AbstractCanvasHandler canvasHandler) {
        proxy.setCanvasHandler(canvasHandler);
        return this;
    }

    public ConnectorProxy setEdge(final Edge<? extends ViewConnector<?>, Node> edge) {
        this.edge = edge;
        return this;
    }

    public ConnectorProxy setSourceNode(final Node<? extends View<?>, Edge> sourceNode) {
        this.sourceNode = sourceNode;
        return this;
    }

    public ConnectorProxy start(final AbstractMouseEvent event) {
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
        edge = null;
        sourceNode = null;
    }

    private EdgeShape onCreateProxy() {
        final CanvasCommandFactory<AbstractCanvasHandler> commandFactory = proxy.lookupCanvasFactory();
        proxy.execute(commandFactory.addConnector(sourceNode,
                                                  edge,
                                                  MagnetConnection.Builder.atCenter(sourceNode),
                                                  getMetadata().getShapeSetId()));

        return getConnector();
    }

    void onKeyDownEvent(final @Observes KeyDownEvent event) {
        proxy.handleCancelKey(event.getKey());
    }

    private EdgeShape getConnector() {
        return (EdgeShape) proxy.getCanvas().getShape(edge.getUUID());
    }

    private Metadata getMetadata() {
        return proxy.getCanvasHandler().getDiagram().getMetadata();
    }
}
