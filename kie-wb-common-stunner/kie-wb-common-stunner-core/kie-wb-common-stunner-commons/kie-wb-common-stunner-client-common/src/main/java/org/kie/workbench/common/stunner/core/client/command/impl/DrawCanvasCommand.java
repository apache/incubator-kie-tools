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

import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

import java.util.List;

public final class DrawCanvasCommand extends AbstractCanvasCommand {

    private final TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    public DrawCanvasCommand( final TreeWalkTraverseProcessor treeWalkTraverseProcessor ) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
    }

    @Override
    public CommandResult<CanvasViolation> execute( final AbstractCanvasHandler context ) {
        draw( context );
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo( final AbstractCanvasHandler context ) {
        // TODO: Return to previous snapshot? Use CleanCanvasCommand?
        return null;
    }

    private void draw( final AbstractCanvasHandler context ) {
        final ShapeManager shapeManager = context.getShapeManager();
        final Diagram diagram = context.getDiagram();
        // Walk throw the graph and register the shapes.
        treeWalkTraverseProcessor
                .useEdgeVisitorPolicy( TreeWalkTraverseProcessor.EdgeVisitorPolicy.VISIT_EDGE_AFTER_TARGET_NODE )
                .traverse( diagram.getGraph(), new AbstractTreeTraverseCallback<Graph, Node, Edge>() {
                    @Override
                    public void startGraphTraversal( final Graph graph ) {
                    }

                    @Override
                    @SuppressWarnings( "unchecked" )
                    public boolean startNodeTraversal( final Node node ) {
                        if ( node.getContent() instanceof View ) {
                            if ( !CanvasLayoutUtils.isCanvasRoot( diagram, node ) ) {
                                final View viewContent = ( View ) node.getContent();
                                final ShapeFactory factory =
                                        shapeManager.getFactory( getDefinitionId( context, viewContent.getDefinition() ) );
                                // Add the node shape into the canvas.
                                context.register( factory, node );
                                context.applyElementMutation( node, MutationContext.STATIC );

                            }
                            return true;

                        }
                        return false;
                    }

                    @Override
                    @SuppressWarnings( "unchecked" )
                    public boolean startEdgeTraversal( final Edge edge ) {
                        final Object content = edge.getContent();
                        if ( content instanceof View ) {
                            final View viewContent = ( View ) edge.getContent();
                            final ShapeFactory factory =
                                    shapeManager.getFactory( getDefinitionId( context, viewContent.getDefinition() ) );
                            // Add the edge shape into the canvas.
                            context.register( factory, edge );
                            context.applyElementMutation( edge, MutationContext.STATIC );
                            ShapeUtils.applyConnections( edge, context, MutationContext.STATIC );
                            return true;

                        } else if ( content instanceof Child ) {
                            final Node child = edge.getTargetNode();
                            final Node parent = edge.getSourceNode();
                            // If the child is docked, do not consider adding it as a child shape of the given parent here,
                            // as docking lienzo wires implies that child shape is added as a child for the docked shape.
                            // TODO: This logic is specific for lienzo wires, should not be here.
                            if ( !isDocked( child ) ) {
                                final Object childContent = child.getContent();
                                if ( childContent instanceof View ) {
                                    context.addChild( parent, child );
                                    context.applyElementMutation( child, MutationContext.STATIC );
                                }

                            }
                            return true;

                        } else if ( content instanceof Dock ) {
                            final Node docked = edge.getTargetNode();
                            final Node parent = edge.getSourceNode();
                            final Object dockedContent = docked.getContent();
                            if ( dockedContent instanceof View ) {
                                context.dock( parent, docked );
                                context.applyElementMutation( docked, MutationContext.STATIC );
                            }
                            return true;
                        }
                        return false;

                    }

                    @Override
                    public void endGraphTraversal() {
                        super.endGraphTraversal();
                        // Draw the canvas shapes.
                        context.getCanvas().draw();

                    }

                } );

    }

    @SuppressWarnings( "unchecked" )
    private boolean isDocked( final Node child ) {
        if ( null != child ) {
            List<Edge> edges = child.getInEdges();
            if ( null != edges && !edges.isEmpty() ) {
                for ( final Edge edge : edges ) {
                    if ( isDockEdged( edge ) ) {
                        return true;
                    }
                }

            }

        }
        return false;
    }

    private boolean isDockEdged( final Edge edge ) {
        return edge.getContent() instanceof Dock;
    }

    private String getDefinitionId( final AbstractCanvasHandler context,
                                    final Object definition ) {
        return context.getClientDefinitionManager().adapters().forDefinition().getId( definition );
    }

}
