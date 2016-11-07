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
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.util.List;

public final class DeleteCanvasNodeCommand extends DeleteCanvasElementCommand<Node> {

    public DeleteCanvasNodeCommand( final Node candidate ) {
        super( candidate );
    }

    public DeleteCanvasNodeCommand( final Node candidate, final Node parent ) {
        super( candidate, parent );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void doDeregister( AbstractCanvasHandler context ) {
        if ( null != parent ) {
            context.removeChild( parent.getUUID(), candidate.getUUID() );
        }
        super.doDeregister( context );
    }

    // TODO: Support for multiple parents.
    @Override
    @SuppressWarnings( "unchecked" )
    protected Node getParent() {
        List<Edge> inEdges = candidate.getInEdges();
        if ( null != inEdges && !inEdges.isEmpty() ) {
            for ( final Edge edge : inEdges ) {
                if ( isChildEdge( edge ) || isDockEdge( edge ) ) {
                    return edge.getSourceNode();
                }

            }
        }
        return null;
    }

    private boolean isChildEdge( final Edge edge ) {
        return edge.getContent() instanceof Child;
    }

    private boolean isDockEdge( final Edge edge ) {
        return edge.getContent() instanceof Dock;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> buildGraphCommand( final AbstractCanvasHandler context ) {
        return new SafeDeleteNodeCommand( candidate.getUUID() );
    }

    @Override
    protected AbstractCanvasCommand buildUndoCommand( final AbstractCanvasHandler context ) {
        return parent != null ?
                new AddCanvasChildNodeCommand( parent, candidate, factory ) :
                new AddCanvasNodeCommand( candidate, factory );
    }

}
