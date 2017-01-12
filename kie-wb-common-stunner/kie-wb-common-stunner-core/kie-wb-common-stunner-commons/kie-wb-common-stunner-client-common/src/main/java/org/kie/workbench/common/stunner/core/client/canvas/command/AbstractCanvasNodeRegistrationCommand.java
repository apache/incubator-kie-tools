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
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

import java.util.logging.Logger;

/**
 * Base canvas command that adds a node and its children, if any, into the canvas, by aggregating
 * canvas commands into a composite one, which finally is executed.
 */
public abstract class AbstractCanvasNodeRegistrationCommand extends AbstractCanvasCommand {

    private static Logger LOGGER = Logger.getLogger( AbstractCanvasNodeRegistrationCommand.class.getName() );

    private final TreeWalkTraverseProcessor treeWalkTraverseProcessor;
    private final Node node;
    private Command<AbstractCanvasHandler, CanvasViolation> command;

    protected AbstractCanvasNodeRegistrationCommand( final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                                     final Node node ) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
        this.node = node;
        this.command = null;
    }

    protected abstract String getShapeSetId( AbstractCanvasHandler context );

    protected abstract boolean registerCandidate( AbstractCanvasHandler context );

    @Override
    public CommandResult<CanvasViolation> execute( final AbstractCanvasHandler context ) {
        final Diagram diagram = context.getDiagram();
        final String shapeSetId = null == getShapeSetId( context ) ?
                context.getDiagram().getMetadata().getShapeSetId() : getShapeSetId( context );
        // Walk throw the graph and register the shapes.
        treeWalkTraverseProcessor
                .useEdgeVisitorPolicy( TreeWalkTraverseProcessor.EdgeVisitorPolicy.VISIT_EDGE_AFTER_TARGET_NODE )
                .traverse( diagram.getGraph(), node, new AbstractTreeTraverseCallback<Graph, Node, Edge>() {

                    private CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation> commandBuilder;

                    @Override
                    public void startGraphTraversal( final Graph graph ) {
                        command = null;
                        commandBuilder = new CompositeCommandImpl.CompositeCommandBuilder<>();
                    }

                    @Override
                    @SuppressWarnings( "unchecked" )
                    public boolean startNodeTraversal( final Node node ) {
                        if ( CanvasLayoutUtils.isCanvasRoot( diagram, node ) ) {
                            return true;
                        }
                        // Register the candidate node.
                        if ( null != AbstractCanvasNodeRegistrationCommand.this.node
                                && AbstractCanvasNodeRegistrationCommand.this.node.equals( node ) ) {
                            return registerCandidate( context );
                        }
                        // Register only visible and candidate's child nodes.
                        if ( node.getContent() instanceof View && isChild( node ) ) {
                            commandBuilder.addCommand( new AddCanvasNodeCommand( node, shapeSetId ) );
                            return true;
                        }
                        return false;
                    }

                    @Override
                    @SuppressWarnings( "unchecked" )
                    public boolean startEdgeTraversal( final Edge edge ) {
                        final Object content = edge.getContent();
                        if ( content instanceof View ) {
                            commandBuilder.addCommand( new AddCanvasConnectorCommand( edge, shapeSetId ) );
                            return true;
                        } else if ( content instanceof Child ) {
                            final Node child = edge.getTargetNode();
                            final Node parent = edge.getSourceNode();
                            final Object childContent = child.getContent();
                            if ( childContent instanceof View ) {
                                commandBuilder.addCommand( new SetCanvasChildNodeCommand( parent, child ) );
                            }
                            return true;
                        } else if ( content instanceof Dock ) {
                            final Node docked = edge.getTargetNode();
                            final Node parent = edge.getSourceNode();
                            final Object dockedContent = docked.getContent();
                            if ( dockedContent instanceof View ) {
                                commandBuilder.addCommand( new CanvasDockNodeCommand( parent, docked ) );
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void endGraphTraversal() {
                        super.endGraphTraversal();
                        if ( commandBuilder.size() > 0 ) {
                            command = commandBuilder.build();
                        }
                    }

                    @SuppressWarnings( "unchecked" )
                    private boolean isChild( final Node<?, Edge> candidate ) {
                        return null == getCandidate() ||
                                candidate.getInEdges().stream()
                                        .filter( edge -> {
                                            if ( edge.getContent() instanceof Child ) {
                                                final Node<?, Edge> parent = edge.getSourceNode();
                                                return null != parent &&
                                                        ( parent.equals( getCandidate() ) || isChild( parent ) );
                                            }
                                            return false;
                                        } )
                                        .findFirst()
                                        .isPresent();
                    }

                } );
        if ( null != command ) {
            return command.execute( context );
        }
        return buildResult();
    }

    public Node getCandidate() {
        return node;
    }

}
