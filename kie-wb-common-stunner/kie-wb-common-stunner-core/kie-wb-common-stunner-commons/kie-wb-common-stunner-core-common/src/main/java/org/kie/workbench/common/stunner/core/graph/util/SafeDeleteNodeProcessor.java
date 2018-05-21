/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;

public class SafeDeleteNodeProcessor {

    private static Logger LOGGER = Logger.getLogger(SafeDeleteNodeProcessor.class.getName());

    public interface Callback {

        void deleteCandidateConnector(final Edge<? extends View<?>, Node> edge);

        boolean deleteConnector(final Edge<? extends View<?>, Node> edge);

        void removeChild(final Element<?> parent,
                         final Node<?, Edge> candidate);

        void removeDock(final Node<?, Edge> parent,
                        final Node<?, Edge> candidate);

        void deleteCandidateNode(final Node<?, Edge> node);

        boolean deleteNode(final Node<?, Edge> node);
    }

    private final Set<String> processedConnectors = new HashSet<String>();
    private final Node<Definition<?>, Edge> candidate;
    private final Graph graph;
    private final ChildrenTraverseProcessor childrenTraverseProcessor;

    public SafeDeleteNodeProcessor(final ChildrenTraverseProcessor childrenTraverseProcessor,
                                   final Graph graph,
                                   final Node<Definition<?>, Edge> candidate) {
        this.childrenTraverseProcessor = childrenTraverseProcessor;
        this.graph = graph;
        this.candidate = candidate;
    }

    @SuppressWarnings("unchecked")
    public void run(final Callback callback) {
        final Deque<Node<View, Edge>> nodes = new ArrayDeque();
        processedConnectors.clear();
        childrenTraverseProcessor
                .setRootUUID(candidate.getUUID())
                .traverse(graph,
                          new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

                              @Override
                              public void startNodeTraversal(final Node<View, Edge> node) {
                                  super.startNodeTraversal(node);
                                  nodes.add(node);
                              }

                              @Override
                              public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                                                final Node<View, Edge> node) {
                                  super.startNodeTraversal(parents,
                                                           node);
                                  nodes.add(node);
                                  return true;
                              }
                          });

        // Process delete for children nodes
        nodes.descendingIterator().forEachRemaining(node -> processNode(node,
                                                                        callback,
                                                                        false));

        // Process candidate's delete.
        processNode(candidate,
                    callback,
                    true);
    }

    @SuppressWarnings("unchecked")
    private void processNode(final Node<?, Edge> node,
                             final Callback callback,
                             final boolean candidate) {
        log("Deleting node [" + node.getUUID() + "]");
        Stream.concat(node.getOutEdges().stream(),
                      node.getInEdges().stream())
                .filter(e -> e.getContent() instanceof View)
                .forEach(e -> deleteConnector(callback,
                                              e,
                                              candidate));
        node.getInEdges().stream()
                .filter(e -> e.getContent() instanceof Child)
                .forEach(e -> callback.removeChild(e.getSourceNode(),
                                                   node));

        GraphUtils.getDockedNodes(node).forEach(docked -> callback.removeDock(node, docked));

        if (candidate) {
            callback.deleteCandidateNode(node);
        } else {
            callback.deleteNode(node);
        }
    }

    private void deleteConnector(final Callback callback,
                                 final Edge<? extends View<?>, Node> edge,
                                 final boolean candidate) {
        if (!processedConnectors.contains(edge.getUUID())) {
            if (candidate) {
                callback.deleteCandidateConnector(edge);
            } else {
                callback.deleteConnector(edge);
            }
            processedConnectors.add(edge.getUUID());
        }
    }

    private void log(final String message) {
        LOGGER.log(Level.FINE,
                   message);
    }
}
