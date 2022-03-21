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
import org.kie.workbench.common.stunner.core.graph.command.impl.AddControlPointCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ContentTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.sw.autolayout.elkjs.ELKEdge;
import org.kie.workbench.common.stunner.sw.autolayout.elkjs.ELKNode;
import org.kie.workbench.common.stunner.sw.autolayout.elkjs.ELKUtils;
import org.kie.workbench.common.stunner.sw.autolayout.elkjs.ELKWrapper;
import org.uberfire.client.promise.Promises;

public class AutoLayout {

    @SuppressWarnings("all")
    public static Promise<Graph> applyLayout(final Graph graph,
                                             final Promises promises,
                                             final DirectGraphCommandExecutionContext context,
                                             final String rootUUID) {
        //TODO Temporary solution to inject ELK lib into canvas frame
        new ELKWrapper().injectScript();

        //Get ELK processed layout promise
        final Promise<Object> elkLayoutPromise = processELKLayout(graph, rootUUID);

        //Apply ELK layout to graph
        return promises.create((resolve, reject) -> elkLayoutPromise
                .then(elkGraph -> {
                    final ELKNode elkRoot = ELKUtils.parse(elkGraph);
                    final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();

                    //Apply ELKNodes layout values into graph structure
                    updateNodesPosition(elkRoot, graph, layoutCommands);

                    //Create ELKBendPoints in graph structure
                    createControlPoints(elkRoot, layoutCommands);

                    final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all =
                            new CompositeCommand.Builder<>()
                                    .addCommand(layoutCommands.build())
                                    .build();

                    // TODO: Check errors...
                    all.execute(context);

                    resolve.onInvoke(graph);
                    return null;
                }, error -> {
                    //TODO Handle ELK error
                    return null;
                }));
    }

    @SuppressWarnings("all")
    private static Promise<Object> processELKLayout(final Graph graph, final String rootUUID) {
        //ELK root node definition
        final ELKNode[] elkRoot = new ELKNode[]{new ELKNode(rootUUID, ELKUtils.getCanvasTopDownLayoutOptionsObject())};
        final Map<String, String> nodeContainment = new HashMap<>();

        new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()).traverse(graph, new ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
            private Map<String, Boolean> processedNodes = new HashMap<>();

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
                elkRoot[0].addNode(new ELKNode(node.getUUID(),
                                               bounds.getWidth(),
                                               bounds.getHeight()));

                addProcessedNode(node);
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

                final ELKNode elkParent = elkRoot[0].getChild(parent.getUUID());
                //final Bound bound = getBound(node);
                Bounds bounds = node.getContent().getBounds();
                elkParent.addNode(new ELKNode(node.getUUID(), bounds.getWidth(), bounds.getHeight()));

                //Set horizontal layout for containers
                elkParent.setLayoutOptions(ELKUtils.getContainerLeftToRightDownLayoutOptionsObject());

                addNodeParentReference(node, parent);
                addProcessedNode(node);
            }

            private void addProcessedNode(Node<View, Edge> node) {
                processedNodes.put(node.getUUID(), true);
            }

            private void addNodeParentReference(Node<View, Edge> node, Node<View, Edge> parent) {
                nodeContainment.put(node.getUUID(), parent.getUUID());
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

        new ViewTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()).traverse(graph, new ContentTraverseCallback<View<?>, Node<View, Edge>, Edge<View<?>, Node>>() {
            @Override
            public void startGraphTraversal(Graph<DefinitionSet, Node<View, Edge>> graph) {

            }

            @Override
            public void startEdgeTraversal(Edge<View<?>, Node> edge) {
                final ELKEdge elkEdge = new ELKEdge(edge.getUUID(),
                                                    edge.getSourceNode().getUUID(),
                                                    edge.getTargetNode().getUUID());
                String sourceParent = nodeContainment.get(edge.getSourceNode().getUUID());
                String targetParent = nodeContainment.get(edge.getTargetNode().getUUID());

                //if nodes have same parent add edge into parent node structure
                if (null != sourceParent && sourceParent.equals(targetParent)) {
                    elkRoot[0].getChild(sourceParent).addEdge(elkEdge);
                } else {
                    elkRoot[0].addEdge(elkEdge);
                }
            }

            @Override
            public void endEdgeTraversal(Edge<View<?>, Node> edge) {

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

        //DomGlobal.console.log("----->" + Global.JSON.stringify(elkRoot[0]));

        return ELKUtils.processGraph(elkRoot[0]);
    }

    private static BiPredicate<String, String> isCanvasRoot() {
        return (rootUUID, uuid) -> uuid.equals(rootUUID);
    }

    @SuppressWarnings("all")
    private static void updateNodesPosition(final ELKNode elkRoot,
                                            final Graph graph,
                                            final CompositeCommand.Builder layoutCommands) {
        for (ELKNode elkNode : elkRoot.getChildren().asList()) {
            layoutCommands.addCommand(new UpdateElementPositionCommand(graph.getNode(elkNode.getId()),
                                                                       new Point2D(elkNode.getX(), elkNode.getY())));
            if (elkNode.getChildren().length > 0) {
                updateNodesPosition(elkNode, graph, layoutCommands);
            }
        }
    }

    @SuppressWarnings("all")
    private static void createControlPoints(final ELKNode elkNode,
                                            final CompositeCommand.Builder layoutCommands) {
        for (ELKEdge elkEdge : elkNode.getEdges().asList()) {
            int index = -1;
            for (Point2D point : elkEdge.getBendPoints().asList()) {
                layoutCommands.addCommand(new AddControlPointCommand(elkEdge.getId(), new ControlPoint(point), ++index));
            }
        }

        for (ELKNode elkClildNode : elkNode.getChildren().asList()) {
            createControlPoints(elkClildNode, layoutCommands);
        }
    }
}
