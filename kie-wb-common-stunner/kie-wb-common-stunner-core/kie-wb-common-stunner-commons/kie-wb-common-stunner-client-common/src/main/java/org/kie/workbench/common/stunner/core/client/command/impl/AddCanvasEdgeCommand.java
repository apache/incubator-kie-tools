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
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddEdgeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public final class AddCanvasEdgeCommand extends AddCanvasElementCommand<Edge> {

    private final Node parent;

    public AddCanvasEdgeCommand( final Node parent, final Edge candidate, final ShapeFactory factory ) {
        super( candidate, factory );
        this.parent = parent;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<CanvasViolation> doCanvasExecute( final AbstractCanvasHandler context ) {
        CommandResult<CanvasViolation> result = super.doCanvasExecute( context );
        ShapeUtils.applyConnections( candidate, context, MutationContext.STATIC );
        return result;
    }

    @Override
    protected AbstractCanvasCommand buildUndoCommand( final AbstractCanvasHandler context ) {
        return new DeleteCanvasEdgeCommand( candidate );
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> buildGraphCommand( final AbstractCanvasHandler context ) {
        return new AddEdgeCommand( parent.getUUID(), candidate );
    }

}
