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

package org.kie.workbench.common.stunner.core.graph.processing.traverse.tree;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import javax.enterprise.context.Dependent;
import java.util.*;

@Dependent
public final class TreeWalkTraverseProcessorImpl implements TreeWalkTraverseProcessor {

    private Graph graph;
    private EdgeVisitorPolicy edgeVisitorPolicy;
    private StartingNodesPolicy startingNodesPolicy;
    private TreeTraverseCallback<Graph, Node, Edge> callback;
    private final Set<String> processesEdges = new HashSet<String>();
    private final Set<String> processesNodes = new HashSet<String>();

    public TreeWalkTraverseProcessorImpl() {
        this.edgeVisitorPolicy = EdgeVisitorPolicy.VISIT_EDGE_BEFORE_TARGET_NODE;
        this.startingNodesPolicy = StartingNodesPolicy.NO_INCOMING_EDGES;
    }

    @Override
    public TreeWalkTraverseProcessor useEdgeVisitorPolicy( final EdgeVisitorPolicy policy ) {
        this.edgeVisitorPolicy = policy;
        return this;
    }

    @Override
    public TreeWalkTraverseProcessor useStartingNodesPolicy( final StartingNodesPolicy policy ) {
        this.startingNodesPolicy = policy;
        return this;
    }

    @Override
    public void traverse( final Graph graph,
                          final TreeTraverseCallback<Graph, Node, Edge> callback ) {
        this.graph = graph;
        this.callback = callback;
        processesNodes.clear();
        processesEdges.clear();
        startTraverse();
    }

    private void startTraverse() {
        startGraphTraversal();
        endGraphTraversal();
    }

    private void endGraphTraversal() {
        callback.endGraphTraversal();
        this.graph = null;
        this.callback = null;
        this.processesEdges.clear();
        this.processesNodes.clear();
    }

    private void startGraphTraversal() {
        assert graph != null && callback != null;
        doStartGraphTraversal();
        Collection<Node> startingNodes = getStartingNodes( graph );
        if ( !startingNodes.isEmpty() ) {
            for ( Node node : startingNodes ) {
                startNodeTraversal( node );
            }
        }
    }

    private void doStartGraphTraversal() {
        callback.startGraphTraversal( graph );
    }

    @SuppressWarnings( "unchecked" )
    private void startNodeTraversal( final Node graphNode ) {
        final String uuid = graphNode.getUUID();
        if ( !this.processesNodes.contains( uuid ) ) {
            this.processesNodes.add( uuid );
            if ( doStartNodeTraversal( graphNode ) ) {
                List<Edge> outEdges = graphNode.getOutEdges();
                if ( outEdges != null && !outEdges.isEmpty() ) {
                    for ( Edge edge : outEdges ) {
                        startEdgeTraversal( edge );
                    }
                }
            }
            doEndNodeTraversal( graphNode );
        }
    }

    private boolean doStartNodeTraversal( final Node node ) {
        return callback.startNodeTraversal( node );
    }

    private void doEndNodeTraversal( final Node node ) {
        callback.endNodeTraversal( node );
    }

    private void startEdgeTraversal( final Edge edge ) {
        final String uuid = edge.getUUID();
        if ( !this.processesEdges.contains( uuid ) ) {
            processesEdges.add( uuid );
            boolean isTraverNode = true;
            if ( EdgeVisitorPolicy.VISIT_EDGE_BEFORE_TARGET_NODE.equals( edgeVisitorPolicy ) ) {
                isTraverNode = doStartEdgeTraversal( edge );
            }
            if ( isTraverNode ) {
                final Node outNode = edge.getTargetNode();
                if ( outNode != null ) {
                    startNodeTraversal( outNode );
                }
            }
            if ( EdgeVisitorPolicy.VISIT_EDGE_AFTER_TARGET_NODE.equals( edgeVisitorPolicy ) ) {
                doStartEdgeTraversal( edge );
            }
            doEndEdgeTraversal( edge );
        }
    }

    private boolean doStartEdgeTraversal( final Edge edge ) {
        return callback.startEdgeTraversal( edge );
    }

    private void doEndEdgeTraversal( final Edge edge ) {
        callback.endEdgeTraversal( edge );
    }

    @SuppressWarnings( "unchecked" )
    private Collection<Node> getStartingNodes( final Graph graph ) {
        final Collection<Node> result = new LinkedList<Node>();
        final Iterator<Node> nodesIt = graph.nodes().iterator();
        while ( nodesIt.hasNext() ) {
            final Node node = nodesIt.next();
            if ( isStartingNode( node ) ) {
                result.add( node );
            }
        }
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private boolean isStartingNode( final Node node ) {
        final List<Edge> inEdges = node.getInEdges();
        final int c = null != inEdges ? inEdges.size() : 0;
        if ( c > 0 ) {
            if ( StartingNodesPolicy.NO_INCOMING_VIEW_EDGES.equals( startingNodesPolicy ) ) {
                for ( final Edge edge : inEdges ) {
                    if ( edge.getContent() instanceof View ) {
                        return false;

                    }

                }
                return true;

            }
            return false;

        }
        return true;
    }

}
