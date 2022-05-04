/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.autolayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.client.promise.Promises;

public class AutoLayout {

    @SuppressWarnings("all")
    public static Promise<Node> applyLayout(Graph graph,
                                            Node parentNode,
                                            Promises promises,
                                            DirectGraphCommandExecutionContext context,
                                            boolean isSubset) {

                return promises.create((resolve, reject) -> {
                    final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();

                    updateNodesPosition(parentNode.getUUID(), graph, layoutCommands);

                    final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all =
                            new CompositeCommand.Builder<>()
                                    .addCommand(layoutCommands.build())
                                    .build();

                    all.execute(context);
                    resolve.onInvoke(parentNode);
                });
    }

    private static BiPredicate<String, String> isCanvasRoot() {
        return (rootUUID, uuid) -> uuid.equals(rootUUID);
    }

    @SuppressWarnings("all")
    private static void updateNodesPosition(
            String rootUUID,
            final Graph graph,
            final CompositeCommand.Builder layoutCommands) {

        new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()).traverse(graph, new ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
            private Map<String, Boolean> processedNodes = new HashMap<>();
            private double x = 0d;
            private double y = 0d;


            @Override
            public boolean startNodeTraversal(List<Node<View, Edge>> parents, Node<View, Edge> node) {
                final Node parent = parents.get(parents.size() - 1);

                if (isCanvasRoot().test(rootUUID, parent.getUUID())) {
                    addNode(node);
                } else {
                    //Containment
                    addChildNode(parent, node);
                }

                return true;
            }

            private void addNode(final Node<View, Edge> node) {
                //skip in case the node was already processed
                if (processedNodes.containsKey(node.getUUID())) {
                    return;
                }

                Bounds bounds = node.getContent().getBounds();

                layoutCommands.addCommand(new UpdateElementPositionCommand(graph.getNode(node.getUUID()),
                                                                           new Point2D(getNextX(), getNextY())));

                addProcessedNode(node);
            }

            private double getNextY() {
                return y += 150d;
            }

            private double getNextX() {
                return x += 150d;
            }

            //Containment
            private void addChildNode(final Node<View, Edge> parent,
                                      final Node<View, Edge> node) {
                //skip in case the node was already processed
                if (processedNodes.containsKey(node.getUUID()) || isCanvasRoot().test(rootUUID, parent.getUUID())) {
                    return;
                }

                //check whether the parent was processed, is must be processed before child node
                if (!processedNodes.containsKey(parent.getUUID())) {
                    addNode(parent);
                }

//                layoutCommands.addCommand(new UpdateElementPositionCommand(graph.getNode(node.getUUID()),
//                                                                           new Point2D(getNextX(), getNextY())));

                addProcessedNode(node);
            }

            private void addProcessedNode(Node<View, Edge> node) {
                processedNodes.put(node.getUUID(), true);
            }

            @Override
            public void startGraphTraversal(Graph<DefinitionSet, Node<View, Edge>> graph) {
            }

            @Override
            public void startEdgeTraversal(Edge<Child, Node> edge) {
            }

            @Override
            public void endEdgeTraversal(Edge<Child, Node> edge) {
            }

            @Override
            public void startNodeTraversal(Node<View, Edge> node) {
            }

            @Override
            public void endNodeTraversal(Node<View, Edge> node) {
            }

            @Override
            public void endGraphTraversal() {
            }
        });
    }

}
