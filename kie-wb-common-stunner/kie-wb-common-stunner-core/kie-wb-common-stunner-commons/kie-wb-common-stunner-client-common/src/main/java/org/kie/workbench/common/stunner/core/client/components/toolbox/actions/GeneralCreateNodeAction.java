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

import java.lang.annotation.Annotation;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CreateNodeAction;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils.lookup;

@Dependent
@Default
public class GeneralCreateNodeAction implements CreateNodeAction<AbstractCanvasHandler> {

    private final DefinitionUtils definitionUtils;
    private final ClientFactoryManager clientFactoryManager;
    private final CanvasLayoutUtils canvasLayoutUtils;
    private final Event<CanvasSelectionEvent> selectionEvent;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ManagedInstance<DefaultCanvasCommandFactory> canvasCommandFactories;

    @Inject
    public GeneralCreateNodeAction(final DefinitionUtils definitionUtils,
                                   final ClientFactoryManager clientFactoryManager,
                                   final CanvasLayoutUtils canvasLayoutUtils,
                                   final Event<CanvasSelectionEvent> selectionEvent,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final @Any ManagedInstance<DefaultCanvasCommandFactory> canvasCommandFactories) {
        this.definitionUtils = definitionUtils;
        this.clientFactoryManager = clientFactoryManager;
        this.canvasLayoutUtils = canvasLayoutUtils;
        this.selectionEvent = selectionEvent;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactories = canvasCommandFactories;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void executeAction(final AbstractCanvasHandler canvasHandler,
                              final String sourceNodeId,
                              final String targetNodeId,
                              final String connectorId) {

        // Obtain the connector and source node instances for proxying.
        final Element<View<?>> element = (Element<View<?>>) CanvasLayoutUtils.getElement(canvasHandler,
                                                                                         sourceNodeId);
        final Node<View<?>, Edge> sourceNode = element.asNode();
        final Edge<? extends ViewConnector<?>, Node> connector =
                (Edge<? extends ViewConnector<?>, Node>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    connectorId)
                        .asEdge();
        final Node<View<?>, Edge> targetNode =
                (Node<View<?>, Edge>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    targetNodeId)
                        .asNode();

        final Metadata metadata = canvasHandler.getDiagram().getMetadata();
        final Annotation qualifier = definitionUtils.getQualifier(metadata.getDefinitionSetId());
        final CanvasCommandFactory<AbstractCanvasHandler> commandFactory = lookup(canvasCommandFactories, qualifier);

        final DeferredCompositeCommand.Builder builder =
                new DeferredCompositeCommand.Builder()
                        .deferCommand(() -> addNode(canvasHandler,
                                                    commandFactory,
                                                    sourceNode,
                                                    targetNode))
                        .deferCommand(() -> updateNodeLocation(canvasHandler,
                                                               commandFactory,
                                                               sourceNode,
                                                               targetNode))
                        .deferCommand(() -> addEdge(canvasHandler,
                                                    commandFactory,
                                                    sourceNode,
                                                    targetNode,
                                                    connector))
                        .deferCommand(() -> setEdgeTarget(commandFactory,
                                                          connector,
                                                          targetNode,
                                                          sourceNode));

        final CommandResult result =
                sessionCommandManager.execute(canvasHandler,
                                              builder.build());

        if (!CommandUtils.isError(result)) {
            CanvasLayoutUtils.fireElementSelectedEvent(selectionEvent,
                                                       canvasHandler,
                                                       targetNode.getUUID());
        }
    }

    @PreDestroy
    public void destroy() {
        canvasCommandFactories.destroyAll();
    }

    private CanvasCommand<AbstractCanvasHandler> updateNodeLocation(final AbstractCanvasHandler canvasHandler,
                                                                    final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                                                    final Node<View<?>, Edge> sourceNode,
                                                                    final Node<View<?>, Edge> targetNode) {
        // Obtain the candidate locations for the target node.
        final Point2D location = canvasLayoutUtils.getNext(canvasHandler,
                                                           sourceNode,
                                                           targetNode);
        return commandFactory.updatePosition(targetNode,
                                             location);
    }

    private CanvasCommand<AbstractCanvasHandler> addEdge(final CanvasHandler canvasHandler,
                                                         final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                                         final Node<View<?>, Edge> sourceNode,
                                                         final Node<View<?>, Edge> targetNode,
                                                         final Edge<? extends ViewConnector<?>, Node> connector) {
        return commandFactory.addConnector(sourceNode,
                                           connector,
                                           buildConnectionBetween(sourceNode, targetNode),
                                           canvasHandler.getDiagram().getMetadata().getShapeSetId());
    }

    protected MagnetConnection buildConnectionBetween(final Node<View<?>, Edge> sourceNode,
                                                      final Node<View<?>, Edge> targetNode) {
        return MagnetConnection.Builder.forTarget(sourceNode, targetNode);
    }

    private CanvasCommand<AbstractCanvasHandler> setEdgeTarget(final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                                               final Edge<? extends ViewConnector<?>, Node> connector,
                                                               final Node<View<?>, Edge> targetNode,
                                                               final Node<View<?>, Edge> sourceNode) {
        return commandFactory.setTargetNode(targetNode,
                                            connector,
                                            buildConnectionBetween(targetNode, sourceNode));
    }

    private CanvasCommand<AbstractCanvasHandler> addNode(final CanvasHandler canvasHandler,
                                                         final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                                         final Node<View<?>, Edge> sourceNode,
                                                         final Node<View<?>, Edge> targetNode) {
        final Node parent = (Node) GraphUtils.getParent(sourceNode);
        final String shapeSetId = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        if (null != parent) {
            return commandFactory.addChildNode(parent,
                                               targetNode,
                                               shapeSetId);
        }
        return commandFactory.addNode(targetNode,
                                      shapeSetId);
    }
}
