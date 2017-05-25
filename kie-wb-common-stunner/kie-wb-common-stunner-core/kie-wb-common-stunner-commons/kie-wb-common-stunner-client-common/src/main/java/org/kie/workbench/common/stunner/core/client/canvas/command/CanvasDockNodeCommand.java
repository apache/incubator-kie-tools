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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

/**
 * Docks a node shape into thee parent's one in the canvas context.
 */
public class CanvasDockNodeCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node candidate;

    public CanvasDockNodeCommand(final Node parent,
                                 final Node candidate) {
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        // For the canvas side, docking the candidate shape into the parent one implies
        // that it has to be set as a child as well.
        // So first remove the current parent for the candidate shape, if any.
        getCandidate().getInEdges().stream()
                .filter(e -> e.getContent() instanceof Child)
                .findAny()
                .ifPresent(e -> context.removeChild(e.getSourceNode(),
                                                    candidate));
        // Dock the candidate shape into the parent one.
        context.dock(parent,
                     candidate);
        // Update both shape view's attributes.
        context.applyElementMutation(parent,
                                     MutationContext.STATIC);
        context.applyElementMutation(candidate,
                                     MutationContext.STATIC);
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CanvasUndockNodeCommand(parent,
                                           candidate).execute(context);
    }

    public Node getParent() {
        return parent;
    }

    public Node<?, Edge> getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " [parent=" + getParent() + "," +
                " candidate=" + getCandidate() + "]";
    }
}
