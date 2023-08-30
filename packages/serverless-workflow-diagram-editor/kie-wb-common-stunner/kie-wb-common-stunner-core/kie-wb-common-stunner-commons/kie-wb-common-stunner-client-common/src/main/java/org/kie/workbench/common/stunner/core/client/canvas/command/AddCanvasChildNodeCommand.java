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
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * Registers the child shape into the canvas and  add it into the parent's one  as well.
 */
public class AddCanvasChildNodeCommand extends AbstractRegistrationCanvasNodeCommand {

    private final Node parent;

    public AddCanvasChildNodeCommand(final Node parent,
                                     final Node candidate,
                                     final String shapeSetId) {
        super(candidate,
              shapeSetId);
        this.parent = parent;
    }

    @Override
    protected void register(final AbstractCanvasHandler context) {
        super.register(context);
        context.addChild(parent,
                         getCandidate());
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> result = super.execute(context);
        context.applyElementMutation(parent,
                                     MutationContext.STATIC);

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new RemoveCanvasChildrenCommand(parent,
                                                            getCandidate()))
                .addCommand(new DeleteCanvasNodeCommand(getCandidate(),
                                                        parent))
                .build()
                .execute(context);
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + toUUID(parent) + "," +
                "candidate=" + toUUID(getCandidate()) + "," +
                "shapeSet=" + getShapeSetId() + "]";
    }
}
