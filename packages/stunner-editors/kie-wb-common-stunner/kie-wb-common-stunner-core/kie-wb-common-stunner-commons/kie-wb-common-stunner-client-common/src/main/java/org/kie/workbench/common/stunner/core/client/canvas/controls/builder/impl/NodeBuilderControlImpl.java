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


package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequest;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
@Default
public class NodeBuilderControlImpl
        extends AbstractCanvasHandlerControl<AbstractCanvasHandler>
        implements NodeBuilderControl<AbstractCanvasHandler> {

    private final ClientDefinitionManager clientDefinitionManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final AbstractElementBuilderControl elementBuilderControl;

    @Inject
    public NodeBuilderControlImpl(final ClientDefinitionManager clientDefinitionManager,
                                  final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                                  final @Default @Element AbstractElementBuilderControl elementBuilderControl) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.commandFactory = commandFactory;
        this.elementBuilderControl = elementBuilderControl;
    }

    @Override
    protected void doInit() {
        this.elementBuilderControl.init(canvasHandler);
    }

    @Override
    protected void doDestroy() {
        this.elementBuilderControl.destroy();
    }

    @Override
    public void setCommandManagerProvider(final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.elementBuilderControl.setCommandManagerProvider(provider);
    }

    @Override
    public boolean allows(final NodeBuildRequest request) {
        final double x = request.getX();
        final double y = request.getY();
        final Node<? extends View<?>, Edge> node = request.getNode();
        if (null != node) {
            final ElementBuildRequest<AbstractCanvasHandler> request1 = new ElementBuildRequestImpl(x,
                                                                                                    y,
                                                                                                    node.getContent().getDefinition());
            return elementBuilderControl.allows(request1);
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void build(final NodeBuildRequest request,
                      final BuildCallback buildCallback) {
        final double x = request.getX();
        final double y = request.getY();
        final Node<? extends View<?>, Edge> node = request.getNode();
        final Edge<? extends ViewConnector<?>, Node> inEdge = request.getInEdge();
        final Connection sourceConnection = request.getSourceConnection();
        final Connection targetConnection = request.getTargetConnection();
        if (null != node) {
            final Object nodeDef = node.getContent().getDefinition();
            final String nodeId = clientDefinitionManager.adapters().forDefinition().getId(nodeDef).value();
            final ElementBuilderControlImpl ebc = getElementBuilderControl();
            final Node<View<?>, Edge> parent = ebc.getParent(x,
                                                             y);
            final Point2D childCoordinates = ebc.getComputedChildCoordinates(parent,
                                                                             x,
                                                                             y);
            final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
            ebc.getElementCommands(node,
                                   parent,
                                   ebc.getParentAssignment(parent, nodeDef),
                                   childCoordinates.getX(),
                                   childCoordinates.getY(),
                                   new AbstractElementBuilderControl.CommandsCallback() {
                                       @Override
                                       public void onComplete(final String uuid,
                                                              final List<Command<AbstractCanvasHandler, CanvasViolation>> commands) {
                                           final CompositeCommand.Builder commandBuilder = new CompositeCommand.Builder().addCommands(commands);
                                           if (inEdge != null) {
                                               final Object edgeDef = inEdge.getContent().getDefinition();
                                               final String edgeId = clientDefinitionManager.adapters().forDefinition().getId(edgeDef).value();
                                               // The commands to batch for the edge that connects both nodes.
                                               commandBuilder.addCommand(commandFactory.addConnector(inEdge.getSourceNode(),
                                                                                                     inEdge,
                                                                                                     sourceConnection,
                                                                                                     ssid));
                                               commandBuilder.addCommand(commandFactory.setTargetNode(node,
                                                                                                      inEdge,
                                                                                                      targetConnection));
                                           }
                                           final CommandResult<CanvasViolation> results = elementBuilderControl
                                                   .getCommandManager().execute(canvasHandler,
                                                                                commandBuilder.build());

                                           if (!CommandUtils.isError(results)) {
                                               updateConnectorShape(inEdge,
                                                                    node,
                                                                    sourceConnection,
                                                                    targetConnection);
                                           }
                                           buildCallback.onSuccess(uuid);
                                       }

                                       @Override
                                       public void onError(final ClientRuntimeError error) {
                                           buildCallback.onError(error);
                                       }
                                   });
        }
    }

    @SuppressWarnings("unchecked")
    protected void updateConnectorShape(final Edge<? extends ViewConnector<?>, Node> inEdge,
                                        final Node targetNode,
                                        final Connection sourceConnection,
                                        final Connection targetConnection) {
        final ViewConnector connectorContent = (ViewConnector) inEdge.getContent();
        canvasHandler.applyElementMutation(inEdge,
                                           MutationContext.STATIC);
        final EdgeShape edgeShape = (EdgeShape) canvasHandler.getCanvas().getShape(inEdge.getUUID());
        final Node source = inEdge.getSourceNode();
        if (null != source && null != targetNode) {
            final Shape<?> sShape = canvasHandler.getCanvas().getShape(source.getUUID());
            final Shape<?> tShape = canvasHandler.getCanvas().getShape(targetNode.getUUID());
            connectorContent.setSourceConnection(sourceConnection);
            connectorContent.setTargetConnection(targetConnection);
            edgeShape.applyConnections(inEdge,
                                       sShape.getShapeView(),
                                       tShape.getShapeView(),
                                       MutationContext.STATIC);
        }
    }

    protected ElementBuilderControlImpl getElementBuilderControl() {
        return (ElementBuilderControlImpl) elementBuilderControl;
    }
}
