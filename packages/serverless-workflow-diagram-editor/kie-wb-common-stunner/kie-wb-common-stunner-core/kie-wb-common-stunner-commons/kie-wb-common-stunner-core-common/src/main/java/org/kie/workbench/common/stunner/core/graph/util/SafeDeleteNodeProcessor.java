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


package org.kie.workbench.common.stunner.core.graph.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getDockParent;
import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getDockedNodes;
import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.isDockedNode;
import static org.kie.workbench.common.stunner.core.graph.util.NodeDefinitionHelper.getContentDefinitionId;
import static org.kie.workbench.common.stunner.core.graph.util.NodeDefinitionHelper.getDiagramId;

public class SafeDeleteNodeProcessor {

    public interface Callback {

        void deleteCandidateConnector(final Edge<? extends View<?>, Node> edge);

        boolean deleteConnector(final Edge<? extends View<?>, Node> edge);

        void removeChild(final Element<?> parent,
                         final Node<?, Edge> candidate);

        void removeDock(final Node<?, Edge> parent,
                        final Node<?, Edge> candidate);

        void deleteCandidateNode(final Node<?, Edge> node);

        boolean deleteNode(final Node<?, Edge> node);

        default void moveChildToCanvasRoot(final Element<?> canvas, final Node<?, Edge> node) {
            // Nothing
        }
    }

    private final Set<String> processedConnectors = new HashSet<>();
    private final Node<Definition<?>, Edge> candidate;
    private final Graph graph;
    private final ChildrenTraverseProcessor childrenTraverseProcessor;
    private final boolean keepChildren;
    private final Optional<TreeWalkTraverseProcessor> treeWalkTraverseProcessor;
    private final Optional<GraphsProvider> graphsProvider;

    private String candidateDiagramId;
    private String candidateContentDefinitionId;

    public SafeDeleteNodeProcessor(final ChildrenTraverseProcessor childrenTraverseProcessor,
                                   final Graph graph,
                                   final Node<Definition<?>, Edge> candidate) {
        this(childrenTraverseProcessor, graph, candidate, false, null, null);
    }

    public SafeDeleteNodeProcessor(final ChildrenTraverseProcessor childrenTraverseProcessor,
                                   final Graph graph,
                                   final Node<Definition<?>, Edge> candidate,
                                   final boolean keepChildren,
                                   final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                   final GraphsProvider graphsProvider) {
        this.childrenTraverseProcessor = childrenTraverseProcessor;
        this.graph = graph;
        this.candidate = candidate;
        this.keepChildren = keepChildren;
        this.treeWalkTraverseProcessor = Optional.ofNullable(treeWalkTraverseProcessor);
        this.graphsProvider = Optional.ofNullable(graphsProvider);
        init();
    }

    void init() {
        this.candidateContentDefinitionId = getContentDefinitionId(candidate);
        this.candidateDiagramId = getDiagramId(candidate);
    }

    @SuppressWarnings("unchecked")
    public void run(final Callback callback) {
        final Deque<Node<View, Edge>> nodes = createNodesDequeue();
        processedConnectors.clear();

        if (!keepChildren) {
            deleteChildren(callback, nodes);
        }

        // Process candidate's delete.
        processNode(candidate,
                    callback,
                    true);

        graphsProvider.ifPresent(selectedDiagram -> {
            if (selectedDiagram.isGlobalGraphSelected()) {
                deleteGlobalGraphNodes(callback, nodes);
            }
        });
    }

    Deque<Node<View, Edge>> createNodesDequeue() {
        return new ArrayDeque();
    }

    boolean isDuplicatedOnTheCurrentDiagram(final Node node,
                                            final String nodeId,
                                            final String diagramId) {
        return !Objects.equals(candidate, node)
                && Objects.equals(getCandidateDiagramId(), diagramId)
                && Objects.equals(getCandidateContentDefinitionId(), nodeId);
    }

    public String getCandidateDiagramId() {
        return candidateDiagramId;
    }

    public String getCandidateContentDefinitionId() {
        return candidateContentDefinitionId;
    }

    protected void deleteGlobalGraphNodes(final Callback callback,
                                          final Deque<Node<View, Edge>> nodes) {

        treeWalkTraverseProcessor.ifPresent(treeWalk -> {
            graphsProvider.get().getGraphs().stream().forEach(existingGraph -> {
                treeWalk.traverse(existingGraph,
                                  new AbstractTreeTraverseCallback<Graph, Node, Edge>() {
                                      @Override
                                      public boolean startNodeTraversal(final Node node) {
                                          super.startNodeTraversal(node);
                                          return processGlobalNodeForDeletion(node, nodes);
                                      }

                                      @Override
                                      public boolean startEdgeTraversal(final Edge edge) {
                                          super.startEdgeTraversal(edge);
                                          return true;
                                      }
                                  });
            });
        });

        nodes.descendingIterator().forEachRemaining(node -> processNode(node,
                                                                        callback,
                                                                        false));
    }

    boolean processGlobalNodeForDeletion(final Node node,
                                         final Deque<Node<View, Edge>> nodes) {
        final String nodeId = getContentDefinitionId(node);
        final String diagramId = getDiagramId(node);

        if (isDuplicatedOnTheCurrentDiagram(node, nodeId, diagramId)) {
            nodes.clear();
            return false;
        }

        if (Objects.equals(getCandidateContentDefinitionId(), nodeId)
                && !Objects.equals(node, candidate)) {
            nodes.add(node);
        }
        return true;
    }

    protected void deleteChildren(final Callback callback,
                                  final Deque<Node<View, Edge>> nodes) {
        childrenTraverseProcessor
                .setRootUUID(candidate.getUUID())
                .traverse(graph,
                          new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

                              @Override
                              public void startNodeTraversal(final Node<View, Edge> node) {
                                  super.startNodeTraversal(node);
                                  if (isDockedNode(node)) {
                                      //docked nodes will be handled on the #processNode
                                      return;
                                  }
                                  nodes.add(node);
                              }

                              @Override
                              public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                                                final Node<View, Edge> node) {
                                  super.startNodeTraversal(parents,
                                                           node);
                                  if (isDockedNode(node)) {
                                      //docked nodes will be handled on the #processNode
                                      return true;
                                  }
                                  nodes.add(node);
                                  return true;
                              }
                          });

        // Process delete for children nodes
        nodes.descendingIterator().forEachRemaining(node -> processNode(node,
                                                                        callback,
                                                                        false));
    }

    @SuppressWarnings("unchecked")
    void processNode(final Node<?, Edge> node,
                     final Callback callback,
                     final boolean isTheCandidate) {
        //processing recursively docked nodes relative to the current node
        getDockedNodes(node).forEach(docked -> processNode(docked, callback, false));

        getDockParent(node).ifPresent(parent -> callback.removeDock(parent, node));

        Stream.concat(node.getOutEdges().stream(),
                      node.getInEdges().stream())
                .filter(e -> e.getContent() instanceof View)
                .forEach(e -> deleteConnector(callback,
                                              e,
                                              isTheCandidate));
        node.getInEdges().stream()
                .filter(e -> e.getContent() instanceof Child)
                .forEach(e -> callback.removeChild(e.getSourceNode(),
                                                   node));
        if (isTheCandidate) {
            callback.deleteCandidateNode(node);
        } else {
            callback.deleteNode(node);
        }
    }

    private void deleteConnector(final Callback callback,
                                 final Edge<? extends View<?>, Node> edge,
                                 final boolean isTheCandidate) {
        if (!processedConnectors.contains(edge.getUUID())) {
            if (isTheCandidate) {
                callback.deleteCandidateConnector(edge);
            } else {
                callback.deleteConnector(edge);
            }
            processedConnectors.add(edge.getUUID());
        }
    }
}
