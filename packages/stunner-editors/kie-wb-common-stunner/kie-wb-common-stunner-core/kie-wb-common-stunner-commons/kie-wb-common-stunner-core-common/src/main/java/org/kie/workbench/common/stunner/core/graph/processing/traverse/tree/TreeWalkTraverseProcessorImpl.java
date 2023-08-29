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


package org.kie.workbench.common.stunner.core.graph.processing.traverse.tree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.uberfire.mvp.Command;

@Dependent
public final class TreeWalkTraverseProcessorImpl implements TreeWalkTraverseProcessor {

    private Graph graph;
    private TreeTraverseCallback<Graph, Node, Edge> callback;
    private final Set<String> processesEdges = new HashSet<String>();
    private final Set<String> processesNodes = new HashSet<String>();
    private final Set<Edge> pendingEdges = new HashSet<Edge>();
    private Predicate<Node<?, Edge>> startNodePredicate;

    public TreeWalkTraverseProcessorImpl() {
        this.startNodePredicate = n -> n.getInEdges().isEmpty();
    }

    @Override
    public TreeWalkTraverseProcessorImpl useStartNodePredicate(final Predicate<Node<?, Edge>> predicate) {
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
        // Clear instance's caches state.
        processesNodes.clear();
        processesEdges.clear();
        pendingEdges.clear();
        // Start traversing the graph.
        startGraphTraversal(node);
        // Process any remaining edges, if any.
        processPendingEdges();
        // End the graph traversal.
        endGraphTraversal();
        // Clear instance's state.
        this.processesEdges.clear();
        this.pendingEdges.clear();
        this.processesNodes.clear();
        this.graph = null;
        this.callback = null;
    }

    @SuppressWarnings("unchecked")
    private void startGraphTraversal(final Optional<Node<?, Edge>> startNode) {
        callback.startGraphTraversal(graph);
        if (!startNode.isPresent()) {
            final List<Node<?, Edge>> orderedGraphNodes = getStartingNodes();
            for (final Node<?, Edge> node : orderedGraphNodes) {
                ifNotProcessed(node,
                               () -> startNodeTraversal(node));
            }
        } else {
            startNodeTraversal(startNode.get());
        }
    }

    private void endGraphTraversal() {
        callback.endGraphTraversal();
    }

    private void processPendingEdges() {
        pendingEdges.forEach(this::processPendingEdge);
    }

    private void processPendingEdge(final Edge edge) {
        startEdgeTraversal(edge);
    }

    private boolean isEdgeProcessed(final Edge edge) {
        return processesEdges.contains(edge.getUUID());
    }

    @SuppressWarnings("unchecked")
    private void startNodeTraversal(final Node<?, Edge> node) {
        this.processesNodes.add(node.getUUID());
        if (callback.startNodeTraversal(node)) {
            // Outgoing connections.
            node.getOutEdges().forEach(this::startEdgeTraversal);
            callback.endNodeTraversal(node);
            // Outgoing connections.
            pendingEdges.addAll(node.getInEdges());
        }
    }

    @SuppressWarnings("unchecked")
    private void startEdgeTraversal(final Edge edge) {
        final String uuid = edge.getUUID();
        if (!this.processesEdges.contains(uuid)) {
            processesEdges.add(uuid);
            if (callback.startEdgeTraversal(edge)) {
                ifNotProcessed(edge.getTargetNode(),
                               () -> startNodeTraversal(edge.getTargetNode()));
            }
            endEdgeTraversal(edge);
        }
    }

    private void endEdgeTraversal(final Edge edge) {
        callback.endEdgeTraversal(edge);
    }

    private void ifNotProcessed(final Node node,
                                final Command action) {
        if (null != node && !processesNodes.contains(node.getUUID())) {
            action.execute();
        }
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
