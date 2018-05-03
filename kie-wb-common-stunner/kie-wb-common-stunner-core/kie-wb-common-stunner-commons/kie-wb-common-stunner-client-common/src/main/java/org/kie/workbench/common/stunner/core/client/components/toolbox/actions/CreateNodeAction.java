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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * A toolbox action/operation for creating a new node, connector and connections from
 * the source toolbox' element.
 */
@Dependent
public class CreateNodeAction extends AbstractToolboxAction {

    static final String KEY_TITLE = "org.kie.workbench.common.stunner.core.client.toolbox.createNewNode";

    private final ClientFactoryManager clientFactoryManager;
    private final CanvasLayoutUtils canvasLayoutUtils;
    private final Event<CanvasSelectionEvent> selectionEvent;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    private String nodeId;
    private String edgeId;

    @Inject
    public CreateNodeAction(final DefinitionUtils definitionUtils,
                            final ClientFactoryManager clientFactoryManager,
                            final CanvasLayoutUtils canvasLayoutUtils,
                            final Event<CanvasSelectionEvent> selectionEvent,
                            final ClientTranslationService translationService,
                            final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        super(definitionUtils,
              translationService);
        this.clientFactoryManager = clientFactoryManager;
        this.canvasLayoutUtils = canvasLayoutUtils;
        this.selectionEvent = selectionEvent;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
    }

    public String getNodeId() {
        return nodeId;
    }

    public CreateNodeAction setNodeId(final String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public CreateNodeAction setEdgeId(final String edgeId) {
        this.edgeId = edgeId;
        return this;
    }

    @Override
    protected String getTitleKey(final AbstractCanvasHandler canvasHandler,
                                 final String uuid) {
        return KEY_TITLE;
    }

    @Override
    protected String getTitleDefinitionId(final AbstractCanvasHandler canvasHandler,
                                          final String uuid) {
        return nodeId;
    }

    @Override
    protected String getGlyphId(final AbstractCanvasHandler canvasHandler,
                                final String uuid) {
        return nodeId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {

        // Obtain the connector and source node instances for proxying.
        final Element<View<?>> element = (Element<View<?>>) getElement(canvasHandler,
                                                                       uuid);
        final Node<View<?>, Edge> sourceNode = element.asNode();
        final Edge<? extends ViewConnector<?>, Node> connector =
                (Edge<? extends ViewConnector<?>, Node>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    edgeId)
                        .asEdge();
        final Node<View<?>, Edge> targetNode =
                (Node<View<?>, Edge>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    nodeId)
                        .asNode();

        final DeferredCompositeCommand.Builder builder =
                new DeferredCompositeCommand.Builder()
                        .deferCommand(() -> addNode(canvasHandler,
                                                    sourceNode,
                                                    targetNode))
                        .deferCommand(() -> updateNodeLocation(canvasHandler,
                                                               sourceNode,
                                                               targetNode))
                        .deferCommand(() -> addEdge(canvasHandler,
                                                    sourceNode,
                                                    targetNode,
                                                    connector))
                        .deferCommand(() -> setEdgeTarget(canvasHandler,
                                                          connector,
                                                          targetNode,
                                                          sourceNode));

        final CommandResult result =
                sessionCommandManager.execute(canvasHandler,
                                              builder.build());

        if (!CommandUtils.isError(result)) {
            fireElementSelectedEvent(selectionEvent,
                                     canvasHandler,
                                     targetNode.getUUID());
        }

        return this;
    }

    private CanvasCommand<AbstractCanvasHandler> updateNodeLocation(final AbstractCanvasHandler canvasHandler,
                                                                    final Node<View<?>, Edge> sourceNode,
                                                                    final Node<View<?>, Edge> targetNode) {
        // Obtain the candidate locations for the target node.
        final Point2D location = canvasLayoutUtils.getNext(canvasHandler,
                                                           sourceNode,
                                                           targetNode);
        return canvasCommandFactory.updatePosition(targetNode,
                                                   location);
    }

    private CanvasCommand<AbstractCanvasHandler> addEdge(final AbstractCanvasHandler canvasHandler,
                                                         final Node<View<?>, Edge> sourceNode,
                                                         final Node<View<?>, Edge> targetNode,
                                                         final Edge<? extends ViewConnector<?>, Node> connector) {
        return canvasCommandFactory.addConnector(sourceNode,
                                                 connector,
                                                 MagnetConnection.Builder.forElement(sourceNode, targetNode),
                                                 canvasHandler.getDiagram().getMetadata().getShapeSetId());
    }

    private CanvasCommand<AbstractCanvasHandler> setEdgeTarget(final AbstractCanvasHandler canvasHandler,
                                                               final Edge<? extends ViewConnector<?>, Node> connector,
                                                               final Node<View<?>, Edge> targetNode,
                                                               final Node<View<?>, Edge> sourceNode) {
        return canvasCommandFactory.setTargetNode(targetNode,
                                                  connector,
                                                  MagnetConnection.Builder.forElement(targetNode, sourceNode));
    }

    private CanvasCommand<AbstractCanvasHandler> addNode(final AbstractCanvasHandler canvasHandler,
                                                         final Node<View<?>, Edge> sourceNode,
                                                         final Node<View<?>, Edge> targetNode) {
        final Node parent = (Node) GraphUtils.getParent(sourceNode);
        final String shapeSetId = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        if (null != parent) {
            return canvasCommandFactory.addChildNode(parent,
                                                     targetNode,
                                                     shapeSetId);
        }
        return canvasCommandFactory.addNode(targetNode,
                                            shapeSetId);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(edgeId.hashCode(),
                                         nodeId.hashCode());
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof CreateNodeAction) {
            CreateNodeAction other = (CreateNodeAction) o;
            return other.edgeId.equals(edgeId) &&
                    other.nodeId.equals(nodeId);
        }
        return false;
    }
}
