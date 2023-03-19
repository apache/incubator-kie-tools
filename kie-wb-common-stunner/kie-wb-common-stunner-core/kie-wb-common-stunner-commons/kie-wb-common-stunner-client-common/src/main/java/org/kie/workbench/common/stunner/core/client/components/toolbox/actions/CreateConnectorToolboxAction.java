/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.proxies.ConnectorProxy;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.AbstractMouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseMoveEvent;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * A toolbox's action which goal is to create a new connection, as from the
 * toolbox's related node, to any other candidate canvas' node.
 */
@Dependent
@Default
public class CreateConnectorToolboxAction
        extends AbstractToolboxAction
        implements IsToolboxActionDraggable<AbstractCanvasHandler> {

    static final String KEY_TITLE = "org.kie.workbench.common.stunner.core.client.toolbox.createNewConnector";

    private final ClientFactoryManager clientFactoryManager;
    private final ConnectorProxy connectorProxy;

    private String edgeId;

    @Inject
    public CreateConnectorToolboxAction(final DefinitionUtils definitionUtils,
                                        final ClientFactoryManager clientFactoryManager,
                                        final ClientTranslationService translationService,
                                        final ConnectorProxy connectorProxy) {
        super(definitionUtils,
              translationService);
        this.clientFactoryManager = clientFactoryManager;
        this.connectorProxy = connectorProxy;
    }

    public CreateConnectorToolboxAction setEdgeId(final String edgeId) {
        this.edgeId = edgeId;
        return this;
    }

    public String getEdgeId() {
        return edgeId;
    }

    @Override
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        runProxy(canvasHandler, uuid, event);
        return this;
    }

    @Override
    public ToolboxAction<AbstractCanvasHandler> onMoveStart(final AbstractCanvasHandler canvasHandler,
                                                            final String uuid,
                                                            final MouseMoveEvent event) {
        runProxy(canvasHandler, uuid, event);
        return this;
    }

    @SuppressWarnings("unchecked")
    private void runProxy(final AbstractCanvasHandler canvasHandler,
                          final String uuid,
                          final AbstractMouseEvent event) {
        final Element<?> sourceElement = CanvasLayoutUtils.getElement(canvasHandler, uuid);
        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) sourceElement.asNode();
        final Edge<? extends ViewConnector<?>, Node> connector =
                (Edge<? extends ViewConnector<?>, Node>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    edgeId)
                        .asEdge();

        connectorProxy
                .setEdge(connector)
                .setSourceNode(sourceNode)
                .setCanvasHandler(canvasHandler)
                .start(event);
    }

    @Override
    protected String getTitleKey(final AbstractCanvasHandler canvasHandler,
                                 final String uuid) {
        return KEY_TITLE;
    }

    @Override
    protected String getTitleDefinitionId(final AbstractCanvasHandler canvasHandler,
                                          final String uuid) {
        return edgeId;
    }

    @Override
    protected String getGlyphId(final AbstractCanvasHandler canvasHandler,
                                final String uuid) {
        return edgeId;
    }

    @PreDestroy
    public void destroy() {
        connectorProxy.destroy();
        edgeId = null;
    }

    @Override
    public int hashCode() {
        return edgeId.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof CreateConnectorToolboxAction) {
            CreateConnectorToolboxAction other = (CreateConnectorToolboxAction) o;
            return other.edgeId.equals(edgeId);
        }
        return false;
    }
}
