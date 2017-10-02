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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
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
    private static Logger LOGGER = Logger.getLogger(CreateNodeAction.class.getName());
    private final ClientFactoryManager clientFactoryManager;
    private final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl;
    private final CanvasLayoutUtils canvasLayoutUtils;
    private final Event<CanvasElementSelectedEvent> elementSelectedEvent;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    private String nodeId;
    private String edgeId;

    @Inject
    public CreateNodeAction(final DefinitionUtils definitionUtils,
                            final ClientFactoryManager clientFactoryManager,
                            final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                            final CanvasLayoutUtils canvasLayoutUtils,
                            final Event<CanvasElementSelectedEvent> elementSelectedEvent,
                            final ClientTranslationService translationService,
                            final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        super(definitionUtils,
              translationService);
        this.clientFactoryManager = clientFactoryManager;
        this.nodeBuilderControl = nodeBuilderControl;
        this.canvasLayoutUtils = canvasLayoutUtils;
        this.elementSelectedEvent = elementSelectedEvent;
        this.sessionCommandManager = sessionCommandManager;
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
        final Edge<View<?>, Node> connector =
                (Edge<View<?>, Node>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    edgeId)
                        .asEdge();
        final Node<View<?>, Edge> targetNode =
                (Node<View<?>, Edge>) clientFactoryManager
                        .newElement(UUID.uuid(),
                                    nodeId)
                        .asNode();

        // Set the transient connections to the source/target nodes, for further rule evaluations.
        connector.setSourceNode(sourceNode);
        connector.setTargetNode(targetNode);

        // Obtain the candidate locatrions for the target node.
        final Point2D location = canvasLayoutUtils.getNext(canvasHandler,
                                                           sourceNode,
                                                           targetNode);

        // Build both node and connector elements, shapes, etc etc.
        final MagnetConnection sourceConnection = MagnetConnection.Builder.forElement(element);
        final MagnetConnection targetConnection = MagnetConnection.Builder.forElement(targetNode);
        final NodeBuildRequestImpl buildRequest =
                new NodeBuildRequestImpl(location.getX(),
                                         location.getY(),
                                         targetNode,
                                         connector,
                                         sourceConnection,
                                         targetConnection);
        start(canvasHandler);
        nodeBuilderControl.build(buildRequest,
                                 new BuilderControl.BuildCallback() {
                                     @Override
                                     public void onSuccess(final String newNodeUUID) {
                                         fireElementSelectedEvent(elementSelectedEvent,
                                                                  canvasHandler,
                                                                  newNodeUUID);
                                         complete();
                                     }

                                     @Override
                                     public void onError(final ClientRuntimeError error) {
                                         error(error);
                                     }
                                 });
        return this;
    }

    private void start(final AbstractCanvasHandler canvasHandler) {
        nodeBuilderControl.enable(canvasHandler);
        nodeBuilderControl.setCommandManagerProvider(() -> sessionCommandManager);
    }

    private void complete() {
        nodeBuilderControl.setCommandManagerProvider(null);
        nodeBuilderControl.disable();
    }

    private void error(final ClientRuntimeError error) {
        complete();
        LOGGER.log(Level.SEVERE,
                   error.toString());
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
