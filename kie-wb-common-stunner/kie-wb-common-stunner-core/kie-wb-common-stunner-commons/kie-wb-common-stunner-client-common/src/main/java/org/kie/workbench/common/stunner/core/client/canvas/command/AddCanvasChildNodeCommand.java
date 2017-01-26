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
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

/**
 * Registers the child shape into the canvas and  add it into the parent's one  as well.
 */
public class AddCanvasChildNodeCommand extends AbstractCanvasNodeRegistrationCommand {

    private final Node parent;
    private final String shapeSetId;

    public AddCanvasChildNodeCommand(final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                     final Node parent,
                                     final Node candidate,
                                     final String shapeSetId) {
        super(treeWalkTraverseProcessor,
              candidate);
        this.parent = parent;
        this.shapeSetId = shapeSetId;
    }

    public AddCanvasChildNodeCommand(final Node parent,
                                     final Node candidate,
                                     final String shapeSetId) {
        super(new TreeWalkTraverseProcessorImpl(),
              candidate);
        this.parent = parent;
        this.shapeSetId = shapeSetId;
    }

    @Override
    protected String getShapeSetId(final AbstractCanvasHandler context) {
        return shapeSetId;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean registerCandidate(final AbstractCanvasHandler context) {
        context.register(shapeSetId,
                         getCandidate());
        context.addChild(parent,
                         getCandidate());
        context.applyElementMutation(parent,
                                     MutationContext.STATIC);
        context.applyElementMutation(getCandidate(),
                                     MutationContext.STATIC);
        return false;
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new RemoveCanvasChildCommand(parent,
                                                         getCandidate()))
                .addCommand(new DeleteCanvasNodeCommand(getCandidate(),
                                                        parent))
                .build()
                .execute(context);
    }

    public Node getParent() {
        return parent;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }
}
