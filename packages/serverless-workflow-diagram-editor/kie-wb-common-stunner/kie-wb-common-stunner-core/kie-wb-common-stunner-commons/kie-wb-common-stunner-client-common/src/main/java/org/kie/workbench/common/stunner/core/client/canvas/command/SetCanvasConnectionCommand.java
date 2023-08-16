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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class SetCanvasConnectionCommand extends AbstractCanvasCommand {

    private final Edge<? extends ViewConnector<?>, Node> edge;

    public SetCanvasConnectionCommand(final Edge<? extends ViewConnector<?>, Node> edge) {
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
        }
        if (null != target) {
            context.notifyCanvasElementUpdated(target);
        }
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new SetCanvasConnectionCommand(edge).execute(context);
    }

    public Edge<? extends View<?>, Node> getEdge() {
        return edge;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + toUUID(edge) + "," +
                "sourceNode=" + toUUID(edge.getSourceNode()) + "," +
                "targetNode=" + toUUID(edge.getTargetNode()) + "]";
    }
}
