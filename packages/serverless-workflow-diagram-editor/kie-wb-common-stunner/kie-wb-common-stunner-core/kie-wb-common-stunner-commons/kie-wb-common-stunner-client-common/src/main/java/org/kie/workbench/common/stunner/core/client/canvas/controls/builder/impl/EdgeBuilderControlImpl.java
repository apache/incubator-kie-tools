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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.EdgeBuildRequest;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
@Default
public class EdgeBuilderControlImpl
        extends AbstractCanvasHandlerControl<AbstractCanvasHandler>
        implements EdgeBuilderControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(EdgeBuilderControlImpl.class.getName());

    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Inject
    public EdgeBuilderControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> commandFactory) {
        this.commandFactory = commandFactory;
    }

    @Override
    public void setCommandManagerProvider(final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    public boolean allows(final EdgeBuildRequest request) {
        final Edge<? extends ViewConnector<?>, Node> edge = (Edge<? extends ViewConnector<?>, Node>) request.getEdge();
        final AbstractCanvasHandler<?, ?> wch = canvasHandler;
        final Node<? extends View<?>, Edge> inNode = request.getInNode();
        final Node<? extends View<?>, Edge> outNode = request.getOutNode();
        boolean allowsSourceConn = true;
        if (null != inNode) {
            final CommandResult<CanvasViolation> cr1 = getCommandManager().allow(wch,
                                                                                 commandFactory.setSourceNode(inNode,
                                                                                                              edge,
                                                                                                              MagnetConnection.Builder.forTarget(inNode, outNode)));
            allowsSourceConn = isAllowed(cr1);
        }
        boolean allowsTargetConn = true;
        if (null != outNode) {
            final CommandResult<CanvasViolation> cr2 = getCommandManager().allow(wch,
                                                                                 commandFactory.setTargetNode(outNode,
                                                                                                              edge,
                                                                                                              MagnetConnection.Builder.forTarget(outNode, inNode)));
            allowsTargetConn = isAllowed(cr2);
        }
        return allowsSourceConn && allowsTargetConn;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void build(final EdgeBuildRequest request,
                      final BuildCallback buildCallback) {
        final double x = request.getX();
        final double y = request.getY();
        final Edge<? extends ViewConnector<?>, Node> edge = request.getEdge();
        final AbstractCanvasHandler<?, ?> wch = canvasHandler;
        final Node<? extends View<?>, Edge> inNode = request.getInNode();
        final Node<? extends View<?>, Edge> outNode = request.getOutNode();
        final Canvas canvas = canvasHandler.getCanvas();
        if (null == inNode) {
            throw new RuntimeException(" An edge must be into the outgoing edges list from a node.");
        }
        final String ssid = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        final CompositeCommand.Builder commandBuilder = new CompositeCommand.Builder()
                .addCommand(commandFactory.addConnector(inNode,
                                                        edge,
                                                        MagnetConnection.Builder.forTarget(inNode, outNode),
                                                        ssid));
        if (null != outNode) {
            commandBuilder.addCommand(commandFactory.setTargetNode(outNode,
                                                                   edge,
                                                                   MagnetConnection.Builder.forTarget(outNode, inNode)));
        }
        final CommandResult<CanvasViolation> results = getCommandManager().execute(wch,
                                                                                   commandBuilder.build());
        if (CommandUtils.isError(results)) {
            LOGGER.log(Level.WARNING,
                       results.toString());
        }
        canvasHandler.applyElementMutation(edge,
                                           MutationContext.STATIC);
        buildCallback.onSuccess(edge.getUUID());
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        commandManagerProvider = null;
    }

    private boolean isAllowed(CommandResult<CanvasViolation> result) {
        return !CommandResult.Type.ERROR.equals(result.getType());
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }
}
