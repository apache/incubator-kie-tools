/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * Registers the child shape into the canvas and  add it into the parent's one  as well.
 */
public class AddCanvasChildNodeCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node candidate;
    private final String shapeSetId;

    public AddCanvasChildNodeCommand(final Node parent,
                                     final Node candidate,
                                     final String shapeSetId) {
        this.parent = parent;
        this.candidate = candidate;
        this.shapeSetId = shapeSetId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        context.register(shapeSetId,
                         candidate);
        context.addChild(parent,
                         candidate);
        context.applyElementMutation(parent,
                                     MutationContext.STATIC);
        context.applyElementMutation(candidate,
                                     MutationContext.STATIC);
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new RemoveCanvasChildCommand(parent,
                                                         candidate))
                .addCommand(new DeleteCanvasNodeCommand(candidate,
                                                        parent))
                .build()
                .execute(context);
    }

    public Node getParent() {
        return parent;
    }

    public Node getCandidate() {
        return candidate;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " [parent=" + getUUID(parent) + "," +
                " candidate=" + getUUID(candidate) + "," +
                " shapeSet=" + shapeSetId + "]";
    }
}
