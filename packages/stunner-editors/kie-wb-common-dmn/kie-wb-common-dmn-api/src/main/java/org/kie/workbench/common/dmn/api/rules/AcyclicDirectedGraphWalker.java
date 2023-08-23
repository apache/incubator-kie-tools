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
package org.kie.workbench.common.dmn.api.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

@DMNEditor
public class AcyclicDirectedGraphWalker implements TreeWalkTraverseProcessor {

    private Graph graph;
    private TreeTraverseCallback<Graph, Node, Edge> callback;
    private Predicate<Node<?, Edge>> startNodePredicate;

    private final Node<?, Edge> source;
    private final Node<?, Edge> target;
    private final Edge<?, Node> connector;

    public AcyclicDirectedGraphWalker() {
        this(null,
             null,
             null);
    }

    public AcyclicDirectedGraphWalker(final Node<?, Edge> source,
                                      final Node<?, Edge> target,
                                      final Edge<?, Node> connector) {
        this.startNodePredicate = n -> n.getInEdges().isEmpty();
        this.source = source;
        this.target = target;
        this.connector = connector;
    }

    @Override
    public AcyclicDirectedGraphWalker useStartNodePredicate(final Predicate<Node<?, Edge>> predicate) {
        this.startNodePredicate = predicate;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void traverse(final Graph graph,
                         final Node node,
                         final TreeTraverseCallback<Graph, Node, Edge> callback) {
        startTraverse(graph,
                      Optional.ofNullable(node),
                      callback);
    }

    @Override
    public void traverse(final Graph graph,
                         final TreeTraverseCallback<Graph, Node, Edge> callback) {
        startTraverse(graph,
                      Optional.empty(),
                      callback);
    }

    private void startTraverse(final Graph graph,
                               final Optional<Node<?, Edge>> node,
                               final TreeTraverseCallback<Graph, Node, Edge> callback) {
        assert graph != null && callback != null;
        this.graph = graph;
        this.callback = callback;
        // Start traversing the graph.
        startGraphTraversal(node);
        // End the graph traversal.
        endGraphTraversal();
        // Clear instance's state.
        this.graph = null;
        this.callback = null;
    }

    @SuppressWarnings("unchecked")
    private void startGraphTraversal(final Optional<Node<?, Edge>> startNode) {
        callback.startGraphTraversal(graph);
        if (!startNode.isPresent()) {
            final List<Node<?, Edge>> orderedGraphNodes = getStartingNodes();
            for (final Node<?, Edge> node : orderedGraphNodes) {
                startNodeTraversal(node);
            }
        } else {
            startNodeTraversal(startNode.get());
        }
    }

    private void endGraphTraversal() {
        callback.endGraphTraversal();
    }

    @SuppressWarnings("unchecked")
    private void startNodeTraversal(final Node<?, Edge> node) {
        if (callback.startNodeTraversal(node)) {
            // Outgoing connections.
            node.getOutEdges().forEach(this::startEdgeTraversal);
            if (node.equals(source)) {
                startEdgeTraversal(connector);
            }
            callback.endNodeTraversal(node);
        }
    }

    @SuppressWarnings("unchecked")
    private void startEdgeTraversal(final Edge edge) {
        if (callback.startEdgeTraversal(edge)) {
            if (edge.equals(connector)) {
                startNodeTraversal(target);
            } else {
                final Node targetNode = edge.getTargetNode();
                if (Objects.nonNull(targetNode)) {
                    startNodeTraversal(targetNode);
                }
            }
        }
        endEdgeTraversal(edge);
    }

    private void endEdgeTraversal(final Edge edge) {
        callback.endEdgeTraversal(edge);
    }

    @SuppressWarnings("unchecked")
    private List<Node<?, Edge>> getStartingNodes() {
        final Iterable<Node> nodes = graph.nodes();
        final List<Node<?, Edge>> result = new LinkedList<>();
        nodes.forEach(n -> {
            if (isStartingNode(n)) {
                result.add(n);
            }
        });
        return result;
    }

    @SuppressWarnings("unchecked")
    private boolean isStartingNode(final Node node) {
        return null == node.getInEdges() || startNodePredicate.test(node);
    }
}
