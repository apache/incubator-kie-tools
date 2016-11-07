/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddChildEdgeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * TODO: Register the new edge into the canvas handler's index for the graph.
 */
public final class AddCanvasChildEdgeCommand extends AbstractCanvasGraphCommand {

    private final Node parent;
    private final Node candidate;

    public AddCanvasChildEdgeCommand( final Node parent,
                                      final Node candidate ) {
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    public CommandResult<CanvasViolation> doCanvasExecute( final AbstractCanvasHandler context ) {
        context.addChild( parent, candidate );
        context.applyElementMutation( candidate, MutationContext.STATIC );
        return buildResult();
    }

    @Override
    protected AbstractCanvasCommand buildUndoCommand( final AbstractCanvasHandler context ) {
        return new DeleteCanvasChildEdgeCommand( parent, candidate );
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> buildGraphCommand( AbstractCanvasHandler canvasHandler ) {
        return new AddChildEdgeCommand( parent.getUUID(), candidate.getUUID() );
    }

}
