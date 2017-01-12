/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;

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
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This class is a basic implementation for achieving a simple layout mechanism.
 * It finds an empty area on the canvas where new elements could be place as:
 * - Calling <code>getNextLayoutPosition( CanvasHandler canvasHandler )</code> returns the
 * cartesian coordinates for an empty area found in the diagram's graph that is being managed by the
 * canvas handler instance.
 * - Calling <code>getNextLayoutPosition( CanvasHandler canvasHandler, Element<View<?>> source )</code> returns the
 * cartesian coordinates for an empty area found in the diagram's graph but relative to the given <code>source</code>
 * argument and its parent, if any.
 * In both cases the resulting coordinates are given from the coordinates of the visible element in the graph, which
 * is position is on bottom right rather than the others, plus a given <code>PADDING</code> anb some
 * error margin given by the <code>MARGIN</code> floating point.
 * <p/>
 * TODO: This has to be refactored by the use of a good impl that achieve good dynamic layouts. Probably each
 * Definition Set / Diagram will require a different layout manager as well.
 */
@Dependent
public class CanvasLayoutUtils {

    private static Logger LOGGER = Logger.getLogger( CanvasLayoutUtils.class.getName() );

    private static final int PADDING = 50;
    private static final float MARGIN = 0.2f;

    public class LayoutBoundExceededException extends RuntimeException {

        private final double x;
        private final double y;
        private final double maxX;
        private final double maxY;

        public LayoutBoundExceededException( final double x,
                                             final double y,
                                             final double maxX,
                                             final double maxY ) {
            this.x = x;
            this.y = y;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    public static boolean isCanvasRoot( final Diagram diagram,
                                        final Element parent ) {
        return null != parent && isCanvasRoot( diagram,
                                               parent.getUUID() );
    }

    public static boolean isCanvasRoot( final Diagram diagram,
                                        final String pUUID ) {
        final String canvasRoot = diagram.getMetadata().getCanvasRootUUID();
        return ( null != canvasRoot && null != pUUID && canvasRoot.equals( pUUID ) );
    }

    @SuppressWarnings( "unchecked" )
    public double[] getNext( final CanvasHandler canvasHandler,
                             final double width,
                             final double height ) {
        checkNotNull( "canvasHandler",
                      canvasHandler );
        final Bounds bounds = getGraphBounds( canvasHandler );
        final Bounds.Bound ul = bounds.getUpperLeft();
        final String ruuid = canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
        if ( null != ruuid ) {
            Node root = canvasHandler.getDiagram().getGraph().getNode( ruuid );
            return getNext( canvasHandler,
                            root,
                            width,
                            height,
                            ul.getX(),
                            ul.getY() );
        }
        final Iterable<Node> nodes = canvasHandler.getDiagram().getGraph().nodes();
        if ( null != nodes ) {
            final Bounds.Bound lr = bounds.getLowerRight();
            final List<Node<View<?>, Edge>> nodeList = new LinkedList<>();
            nodes.forEach( nodeList::add );
            return getNext( canvasHandler,
                            nodeList,
                            width,
                            height,
                            ul.getX(),
                            ul.getY(),
                            lr.getX() - PADDING,
                            lr.getY() - PADDING );
        }
        return new double[]{ ul.getX(), ul.getY() };
    }

    @SuppressWarnings( "unchecked" )
    public double[] getNext( final CanvasHandler canvasHandler,
                             final double w,
                             final double h,
                             final double minX,
                             final double minY ) {
        checkNotNull( "canvasHandler",
                      canvasHandler );
        final String ruuid = canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
        if ( null != ruuid ) {
            Node root = canvasHandler.getDiagram().getGraph().getNode( ruuid );
            return getNext( canvasHandler,
                            root,
                            w,
                            h,
                            minX,
                            minY );
        }
        final Bounds bounds = getGraphBounds( canvasHandler );
        final Bounds.Bound lr = bounds.getLowerRight();
        final Iterable<Node> nodes = canvasHandler.getDiagram().getGraph().nodes();
        if ( null != nodes ) {
            final List<Node<View<?>, Edge>> nodeList = new LinkedList<>();
            nodes.forEach( nodeList::add );
            return getNext( canvasHandler,
                            nodeList,
                            w,
                            h,
                            minX,
                            minY,
                            lr.getX() - PADDING,
                            lr.getY() - PADDING );
        }
        return new double[]{ minX, minY };
    }

    @SuppressWarnings( "unchecked" )
    public double[] getNext( final CanvasHandler canvasHandler,
                             final Node<View<?>, Edge> root ) {
        final double[] rootBounds = getBoundCoordinates( root.getContent() );
        final double[] size = GraphUtils.getSize( root.getContent() );

        return getNext( canvasHandler,
                        root,
                        size[ 0 ],
                        size[ 1 ],
                        rootBounds[ 0 ],
                        rootBounds[ 1 ] );
    }

    @SuppressWarnings( "unchecked" )
    public double[] getNext( final CanvasHandler canvasHandler,
                             final Node<View<?>, Edge> root,
                             final double w,
                             final double h,
                             final double minX,
                             final double minY ) {
        checkNotNull( "canvasHandler",
                      canvasHandler );
        checkNotNull( "root",
                      root );
        final List<Edge> outEdges = root.getOutEdges();
        if ( null != outEdges ) {
            final List<Node<View<?>, Edge>> nodes = new LinkedList<>();
            outEdges.stream().forEach( edge -> {
                if ( edge instanceof Child
                        && edge.getTargetNode().getContent() instanceof View ) {
                    nodes.add( edge.getTargetNode() );
                }
            } );
            if ( !nodes.isEmpty() ) {
                final double[] rootBounds = getBoundCoordinates( root.getContent() );
                final double[] n = getNext( canvasHandler,
                                            nodes,
                                            w,
                                            h,
                                            minX,
                                            minY,
                                            rootBounds[ 0 ] - PADDING,
                                            rootBounds[ 1 ] - PADDING );
                return new double[]{ n[ 0 ] + PADDING, n[ 1 ] };
            }
        }
        final Bounds bounds = getGraphBounds( canvasHandler );
        final Bounds.Bound lr = bounds.getLowerRight();
        return check( minX,
                      minY,
                      w,
                      h,
                      minX,
                      minY,
                      lr.getX() - PADDING,
                      lr.getY() - PADDING );
    }

    private double[] getNext( final CanvasHandler canvasHandler,
                              final List<Node<View<?>, Edge>> nodes,
                              final double width,
                              final double height,
                              final double minX,
                              final double minY,
                              final double maxX,
                              final double maxY ) {
        checkNotNull( "canvasHandler",
                      canvasHandler );
        checkNotNull( "nodes",
                      nodes );
        final double[] result = new double[]{ minX, minY };
        nodes.stream().forEach( node -> {
            final double[] coordinates = getAbsolute( node );
            result[ 0 ] = coordinates[ 0 ] >= result[ 0 ] ? coordinates[ 0 ] : result[ 0 ];
            result[ 1 ] = coordinates[ 1 ] >= result[ 1 ] ? coordinates[ 1 ] : result[ 1 ];
            final double[] r = check( coordinates[ 0 ],
                                      coordinates[ 1 ],
                                      width,
                                      height,
                                      minX,
                                      minY,
                                      maxX,
                                      maxY );
            if ( ( coordinates[ 0 ] + width ) >= maxX ) {
                result[ 0 ] = r[ 0 ];
                result[ 1 ] = r[ 1 ];
            }
            if ( ( result[ 1 ] + height ) > maxX ) {
                throw new LayoutBoundExceededException( result[ 0 ],
                                                        result[ 1 ],
                                                        maxX,
                                                        maxY );
            }
        } );
        return result;
    }

    private double[] check( final double x,
                            final double y,
                            final double w,
                            final double h,
                            final double lx,
                            final double ly,
                            final double ux,
                            final double uy ) {
        final double[] result = new double[]{ x, y };
        if ( ( x + w ) >= ux ) {
            result[ 0 ] = lx;
            result[ 1 ] += y + PADDING;
        }
        if ( ( y + h ) > uy ) {
            throw new LayoutBoundExceededException( result[ 0 ],
                                                    result[ 1 ],
                                                    ux,
                                                    uy );
        }
        return new double[]{ result[ 0 ] + PADDING, result[ 1 ] };
    }

    @SuppressWarnings( "unchecked" )
    private double[] getAbsolute( final Node<View<?>, Edge> root ) {
        final double[] pos = getBoundCoordinates( root.getContent() );
        return getAbsolute( root,
                            pos[ 0 ],
                            pos[ 1 ] );
    }

    @SuppressWarnings( "unchecked" )
    private double[] getAbsolute( final Node<View<?>, Edge> root,
                                  final double x,
                                  final double y ) {
        Element parent = GraphUtils.getParent( root );
        if ( null != parent
                && parent instanceof Node
                && parent.getContent() instanceof View ) {
            final double[] pos = getBoundCoordinates( ( View ) parent.getContent() );
            return getAbsolute( ( Node<View<?>, Edge> ) parent,
                                x + pos[ 0 ],
                                y + pos[ 1 ] );
        }
        return new double[]{ x, y };
    }

    private double[] getBoundCoordinates( final View view ) {
        final Bounds bounds = view.getBounds();
        final Bounds.Bound ulBound = bounds.getUpperLeft();
        final Bounds.Bound lrBound = bounds.getLowerRight();
        final double lrX = lrBound.getX();
        final double lrY = ulBound.getY();
        return new double[]{ lrX, lrY };
    }

    @SuppressWarnings( "unchecked" )
    private Bounds getGraphBounds( final CanvasHandler canvasHandler ) {
        final Graph<DefinitionSet, ?> graph = canvasHandler.getDiagram().getGraph();
        return graph.getContent().getBounds();
    }
}