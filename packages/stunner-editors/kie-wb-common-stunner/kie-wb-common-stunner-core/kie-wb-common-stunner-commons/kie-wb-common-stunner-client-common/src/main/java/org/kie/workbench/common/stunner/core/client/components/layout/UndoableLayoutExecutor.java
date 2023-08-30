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


package org.kie.workbench.common.stunner.core.client.components.layout;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layout;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutExecutor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.SizeHandler;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPosition;

/**
 * Copies the layout information to a diagram in a way that it can be reversible.
 */
public class UndoableLayoutExecutor implements LayoutExecutor {

    private final AbstractCanvasHandler canvasHandler;
    private final CanvasCommandManager<AbstractCanvasHandler> commandManager;

    public UndoableLayoutExecutor(final AbstractCanvasHandler canvasHandler,
                                  final CanvasCommandManager<AbstractCanvasHandler> commandManager) {
        this.canvasHandler = canvasHandler;
        this.commandManager = commandManager;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void applyLayout(final Layout layout, final Graph graph, final SizeHandler sizeHandler) {
        if (layout.getNodePositions().size() == 0) {
            return;
        }

        final CompositeCommand command = createCommand(layout, graph);

        commandManager.execute(canvasHandler, command);
    }

    CompositeCommand createCommand(final Layout layout, final Graph graph) {
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();

        for (int i = 0; i < layout.getNodePositions().size(); i++) {
            final VertexPosition position = layout.getNodePositions().get(i);
            final Node node = graph.getNode(position.getId());
            commandBuilder.addCommand(new UpdateElementPositionCommand(node, position.getUpperLeft()));
        }
        return commandBuilder.build();
    }

    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManager;
    }
}