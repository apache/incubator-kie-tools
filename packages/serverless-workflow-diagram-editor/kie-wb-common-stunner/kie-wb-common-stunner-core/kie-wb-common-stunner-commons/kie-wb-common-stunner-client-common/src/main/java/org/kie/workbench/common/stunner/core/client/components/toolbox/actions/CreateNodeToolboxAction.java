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


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.lang.annotation.Annotation;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.proxies.NodeProxy;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseMoveEvent;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils.lookup;

/**
 * A toolbox action/operation for creating a new node, connector and connections from
 * the source toolbox' element.
 */
@Dependent
@GroupActionsToolbox
@FlowActionsToolbox
@Default
public class CreateNodeToolboxAction
        extends AbstractToolboxAction
        implements IsToolboxActionDraggable<AbstractCanvasHandler> {

    static final String KEY_TITLE = "org.kie.workbench.common.stunner.core.client.toolbox.createNewNode";

    private final ManagedInstance<GeneralCreateNodeAction> createNodeActions;
    private final ClientFactoryManager clientFactoryManager;
    private final NodeProxy nodeProxy;

    private String nodeId;
    private String edgeId;

    @Inject
    public CreateNodeToolboxAction(final @Any ManagedInstance<GeneralCreateNodeAction> createNodeActions,
                                   final DefinitionUtils definitionUtils,
                                   final ClientTranslationService translationService,
                                   final ClientFactoryManager clientFactoryManager,
                                   final NodeProxy nodeProxy) {
        super(definitionUtils,
              translationService);
        this.createNodeActions = createNodeActions;
        this.clientFactoryManager = clientFactoryManager;
        this.nodeProxy = nodeProxy;
    }

    public String getNodeId() {
        return nodeId;
    }

    public CreateNodeToolboxAction setNodeId(final String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public CreateNodeToolboxAction setEdgeId(final String edgeId) {
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
    public String getTitle(final AbstractCanvasHandler canvasHandler,
                           final String uuid) {
        final String titleKey = getTitleKey(canvasHandler,
                                            uuid);
        final String titleDefinitionId = getTitleDefinitionId(canvasHandler,
                                                              uuid);
        // TODO: SW Editor - Customize title.
        String nodeTitle = definitionUtils.getTitle(titleDefinitionId);
        String edgeTitle = definitionUtils.getTitle(edgeId);
        return translationService.getValue(titleKey) + " " + edgeTitle + " to " + nodeTitle;
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
        final GeneralCreateNodeAction action = lookupAction(canvasHandler, uuid);
        action.executeAction(canvasHandler,
                             uuid,
                             nodeId,
                             edgeId);
        return this;
    }

    private GeneralCreateNodeAction lookupAction(final AbstractCanvasHandler canvasHandler,
                                                 final String uuid) {
        final Metadata metadata = canvasHandler.getDiagram().getMetadata();
        final Annotation qualifier = getDefinitionUtils().getQualifier(metadata.getDefinitionSetId());
        return lookup(createNodeActions, qualifier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMoveStart(final AbstractCanvasHandler canvasHandler,
                                                            final String uuid,
                                                            final MouseMoveEvent event) {
        final Node<View<?>, Edge> sourceNode = getSourceNode(canvasHandler, uuid);
        final Edge<ViewConnector<?>, Node> connector = buildEdge();
        final Node<View<?>, Edge> targetNode = buildTargetNode();
        nodeProxy
                .setTargetNode(targetNode)
                .setEdge(connector)
                .setSourceNode(sourceNode)
                .setCanvasHandler(canvasHandler)
                .start(event);

        return this;
    }

    @PreDestroy
    public void destroy() {
        createNodeActions.destroyAll();
        nodeProxy.destroy();
        nodeId = null;
        edgeId = null;
    }

    @SuppressWarnings("unchecked")
    private Node<View<?>, Edge> getSourceNode(final AbstractCanvasHandler canvasHandler,
                                              final String uuid) {
        final Element<View<?>> sourceElement = (Element<View<?>>) CanvasLayoutUtils.getElement(canvasHandler,
                                                                                               uuid);
        return sourceElement.asNode();
    }

    @SuppressWarnings("unchecked")
    private Edge<ViewConnector<?>, Node> buildEdge() {
        return (Edge<ViewConnector<?>, Node>) clientFactoryManager
                .newElement(UUID.uuid(),
                            edgeId)
                .asEdge();
    }

    @SuppressWarnings("unchecked")
    private Node<View<?>, Edge> buildTargetNode() {
        return (Node<View<?>, Edge>) clientFactoryManager
                .newElement(UUID.uuid(),
                            nodeId)
                .asNode();
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(edgeId.hashCode(),
                                         nodeId.hashCode());
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof CreateNodeToolboxAction) {
            CreateNodeToolboxAction other = (CreateNodeToolboxAction) o;
            return other.edgeId.equals(edgeId) &&
                    other.nodeId.equals(nodeId);
        }
        return false;
    }
}
