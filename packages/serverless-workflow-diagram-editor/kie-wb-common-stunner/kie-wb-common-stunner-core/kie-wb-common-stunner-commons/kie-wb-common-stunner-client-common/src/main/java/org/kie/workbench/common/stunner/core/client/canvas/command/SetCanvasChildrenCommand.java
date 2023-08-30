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

import java.util.Collection;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;

public class SetCanvasChildrenCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Collection<Node<?, Edge>> candidates;

    public SetCanvasChildrenCommand(final Node parent,
                                    final Collection<Node<?, Edge>> candidates) {
        this.parent = parent;
        this.candidates = candidates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        candidates.forEach(candidate -> setCanvasChild(context, candidate));
        return buildResult();
    }

    @SuppressWarnings("unchecked")
    private void setCanvasChild(final AbstractCanvasHandler context,
                                final Node candidate) {
        // Only update the child on the canvas side if the candidate is not docked.
        if (!isDocked(candidate)) {
            context.addChild(parent,
                             candidate);
            ShapeUtils.moveViewConnectorsToTop(context,
                                               candidate);
        }
    }

    private static boolean isDocked(final Node<?, Edge> candidate) {
        return candidate.getInEdges().stream()
                .anyMatch(e -> e.getContent() instanceof Dock);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new RemoveCanvasChildrenCommand(parent,
                                               candidates)
                .execute(context);
    }

    public Node getParent() {
        return parent;
    }

    public Collection<Node<?, Edge>> getCandidates() {
        return candidates;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + toUUID(parent) + "," +
                "candidates=" + toUUIDs(candidates) + "]";
    }
}
