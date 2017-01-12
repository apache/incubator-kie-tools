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

import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

public final class DrawCanvasCommand extends AbstractCanvasCommand {

    private static Logger LOGGER = Logger.getLogger( DrawCanvasCommand.class.getName() );

    private final TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    public DrawCanvasCommand( final TreeWalkTraverseProcessor treeWalkTraverseProcessor ) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
    }

    @Override
    public CommandResult<CanvasViolation> execute( final AbstractCanvasHandler context ) {
        final Diagram diagram = context.getDiagram();
        final String shapeSetId = getShapeSetId( context );
        final String rootUUID = diagram.getMetadata().getCanvasRootUUID();
        Command<AbstractCanvasHandler, CanvasViolation> command = null;
        if ( null != rootUUID ) {
            final Node root = context.getGraphIndex().getNode( rootUUID );
            command = new AddCanvasNodeCommand( treeWalkTraverseProcessor,
                                                root,
                                                shapeSetId );
        } else {
            command = new CanvasNodesRegistrationCommand();
        }
        return command.execute( context );
    }

    @Override
    public CommandResult<CanvasViolation> undo( final AbstractCanvasHandler context ) {
        throw new UnsupportedOperationException( "Undo operation for [" + this.getClass().getName() + "[ is not supported.." );
    }

    private String getShapeSetId( final AbstractCanvasHandler context ) {
        return context.getDiagram().getMetadata().getShapeSetId();
    }

    /**
     * Registers all nodes.
     */
    private final class CanvasNodesRegistrationCommand extends AbstractCanvasNodeRegistrationCommand {

        private CanvasNodesRegistrationCommand() {
            super( treeWalkTraverseProcessor,
                   null );
        }

        @Override
        protected String getShapeSetId( final AbstractCanvasHandler context ) {
            return DrawCanvasCommand.this.getShapeSetId( context );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        protected boolean registerCandidate( final AbstractCanvasHandler context ) {
            context.register( getShapeSetId( context ),
                              getCandidate() );
            context.applyElementMutation( getCandidate(),
                                          MutationContext.STATIC );
            return true;
        }

        @Override
        public CommandResult<CanvasViolation> undo( final AbstractCanvasHandler context ) {
            return DrawCanvasCommand.this.undo( context );
        }
    }
}
