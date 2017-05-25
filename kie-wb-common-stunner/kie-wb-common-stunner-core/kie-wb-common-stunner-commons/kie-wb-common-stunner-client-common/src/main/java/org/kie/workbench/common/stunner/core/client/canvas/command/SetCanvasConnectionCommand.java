/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SetCanvasConnectionCommand extends AbstractCanvasCommand {

    private static Logger LOGGER = Logger.getLogger(SetCanvasConnectionCommand.class.getName());

    private final Edge<? extends View<?>, Node> edge;

    public SetCanvasConnectionCommand(final Edge<? extends View<?>, Node> edge) {
        this.edge = edge;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final Node source = edge.getSourceNode();
        final Node target = edge.getTargetNode();
        ShapeUtils.applyConnections(edge,
                                    context,
                                    MutationContext.STATIC);
        if (null != source) {
            context.notifyCanvasElementUpdated(source);
            highlightInvalidConnection(context,
                                       edge,
                                       source);
        }
        if (null != target) {
            context.notifyCanvasElementUpdated(target);
            highlightInvalidConnection(context,
                                       edge,
                                       target);
        }
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new SetCanvasConnectionCommand(edge).execute(context);
    }

    public static void highlightInvalidConnection(final AbstractCanvasHandler context,
                                                  final Edge<? extends View<?>, Node> edge,
                                                  final Node<? extends View<?>, Edge> node) {
        final String uuid = edge.getUUID();
        final Shape<?> shape = context.getCanvas().getShape(uuid);
        if (null != shape) {
            final ShapeState state = null != node ? ShapeState.NONE : ShapeState.INVALID;
            LOGGER.log(Level.FINE,
                       "Highlight connector for UUID [" + uuid + "] with state [" + state + "]");
            shape.applyState(state);
        } else {
            LOGGER.log(Level.WARNING,
                       "Cannot highlight connector as it is not found for UUID [" + uuid + "]");
        }
    }

    public Edge<? extends View<?>, Node> getEdge() {
        return edge;
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " [candidate=" + getUUID(edge) + "," +
                " sourceNode=" + getUUID(edge.getSourceNode()) + "," +
                " targetNode=" + getUUID(edge.getTargetNode()) + "]";
    }
}
