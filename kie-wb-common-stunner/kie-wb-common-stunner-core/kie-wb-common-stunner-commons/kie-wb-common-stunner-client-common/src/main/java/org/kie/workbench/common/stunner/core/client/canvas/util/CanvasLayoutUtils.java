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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import javax.enterprise.context.Dependent;
import java.util.Iterator;

// TODO: This has to be refactored by the use of a good canvas layout API.
@Dependent
public class CanvasLayoutUtils {

    private static final int PADDING = 50;
    private static final float MARGIN = 0.2f;

    public static boolean isCanvasRoot( final Diagram diagram,
                                        final Element parent ) {
        return null != parent && isCanvasRoot( diagram, parent.getUUID() );
    }

    public static boolean isCanvasRoot( final Diagram diagram,
                                        final String pUUID ) {
        final String canvasRoot = diagram.getSettings().getCanvasRootUUID();
        return ( null != canvasRoot && null != pUUID && canvasRoot.equals( pUUID ) );
    }

    public double[] getNextLayoutPosition( final CanvasHandler canvasHandler, final Element<View<?>> source ) {
        final Double[] pos = GraphUtils.getPosition( source.getContent() );
        final double[] next = new double[]{ pos[ 0 ], pos[ 1 ] };
        return checkNextLayoutPosition( next[ 0 ], next[ 1 ], canvasHandler );
    }

    public double[] getNextLayoutPosition( final CanvasHandler canvasHandler ) {
        final String ruuid = canvasHandler.getDiagram().getSettings().getCanvasRootUUID();
        final double[] next = getNextLayoutPosition( canvasHandler, ruuid );
        return checkNextLayoutPosition( next[ 0 ], next[ 1 ], canvasHandler );
    }

    // Check that "next" cartesian coordinates are not bigger than current graph bounds.
    @SuppressWarnings( "unchecked" )
    private double[] checkNextLayoutPosition( final double x,
                                              final double y,
                                              final CanvasHandler canvasHandler ) {
        final double[] result = { x, y };
        final Graph<DefinitionSet, ?> graph = canvasHandler.getDiagram().getGraph();
        final Bounds bounds = graph.getContent().getBounds();
        final Bounds.Bound lr = bounds.getLowerRight();
        if ( x >= getBound( lr.getX() ) ) {
            result[ 0 ] = PADDING;
            result[ 1 ] += PADDING;

        }
        if ( result[ 1 ] >= getBound( lr.getY() ) ) {
            throw new RuntimeException( "Diagram bounds exceeded." );

        }
        return result;
    }

    private double getBound( final double bound ) {
        return bound - getMargin( bound );
    }

    private double getMargin( final double size ) {
        return size * MARGIN;
    }

    @SuppressWarnings( "unchecked" )
    private double[] getNextLayoutPosition( final CanvasHandler canvasHandler, final String rootUUID ) {
        final Graph graph = canvasHandler.getDiagram().getGraph();
        final double[] result = new double[ 2 ];
        result[ 0 ] = 0;
        result[ 1 ] = 0;
        new ChildrenTraverseProcessorImpl( new TreeWalkTraverseProcessorImpl() )
                .setRootUUID( rootUUID )
                .traverse( graph, new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

                    @Override
                    public void startNodeTraversal( final Node<View, Edge> node ) {
                        super.startNodeTraversal( node );
                        onStartNodeTraversal( null, node );
                    }

                    @Override
                    public boolean startNodeTraversal( final Iterator<Node<View, Edge>> parents,
                                                       final Node<View, Edge> node ) {
                        super.startNodeTraversal( parents, node );
                        onStartNodeTraversal( parents, node );
                        return true;
                    }

                    private void onStartNodeTraversal( final Iterator<Node<View, Edge>> parents,
                                                       final Node<View, Edge> node ) {
                        if ( null != parents && parents.hasNext() ) {
                            double parentX = 0;
                            double parentY = 0;
                            while ( parents.hasNext() ) {
                                Node tParent = parents.next();
                                final Object content = tParent.getContent();
                                if ( content instanceof View ) {
                                    final View viewContent = ( View ) content;
                                    final Double[] parentCoords = GraphUtils.getPosition( viewContent );
                                    parentX += parentCoords[ 0 ];
                                    parentY += parentCoords[ 1 ];
                                }

                            }
                            final double[] coordinates = getNodeCoordinates( node, parentX, parentY );
                            if ( coordinates[ 0 ] > getCurrentMaxX() ) {
                                result[ 0 ] = coordinates[ 0 ];

                            }
                            if ( coordinates[ 1 ] > getCurrentMaxY() ) {
                                result[ 1 ] = coordinates[ 1 ];

                            }

                        } else if ( null != node ) {
                            final Double[] nodeCoords = GraphUtils.getPosition( node.getContent() );
                            final Double[] nodeSize = GraphUtils.getSize( node.getContent() );
                            if ( nodeCoords[ 0 ] > getCurrentMaxX() ) {
                                result[ 0 ] = nodeCoords[ 0 ] + nodeSize[ 0 ] + PADDING;

                            }
                            if ( nodeCoords[ 1 ] > getCurrentMaxY() ) {
                                result[ 1 ] = nodeCoords[ 1 ] + nodeSize[ 1 ] + PADDING;

                            }

                        }

                    }

                    private double getCurrentMaxX() {
                        return result[ 0 ];
                    }

                    private double getCurrentMaxY() {
                        return result[ 1 ];
                    }

                } );
        return result;
    }

    private double[] getNodeCoordinates( final Node node,
                                         final double parentX,
                                         final double parentY ) {
        final View content = ( View ) node.getContent();
        final Bounds bounds = content.getBounds();
        final Bounds.Bound lrBound = bounds.getLowerRight();
        final double lrX = lrBound.getX() + parentX;
        final double lrY = lrBound.getY() + parentY;
        return new double[]{ lrX, lrY };
    }

}
