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

import elemental2.dom.DomGlobal;
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
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.uberfire.client.promise.Promises;

public class AutoLayout {

    @SuppressWarnings("all")
    public static Promise<Node> applyLayout(Graph graph,
                                            Node parentNode,
                                            Promises promises,
                                            DirectGraphCommandExecutionContext context,
                                            boolean isSubset) {
        //TODO Temporary solution to inject ELK lib into canvas frame
        new ELKWrapper().injectScript();

        //Get ELK processed layout promise
        final Promise<Object> elkLayoutPromise = ELKUtils.
                processGraph(buildElkInputNode(graph,
                                               parentNode,
                                               ELKUtils.getCanvasTopDownLayoutOptionsObject(),
                                               ELKUtils.getContainerLeftToRightDownLayoutOptionsObject(),
                                               isSubset).sortEdges());

        //Apply ELK layout to graph
        return promises.create((resolve, reject) -> elkLayoutPromise
                .then(elkGraph -> {
                    final ELKNode elkRoot = ELKUtils.parse(elkGraph);
                    final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();

                    //Update node sizes in the graph
                    updateGraphNodeSizes(elkRoot, graph);

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

                    resolve.onInvoke(parentNode);
                    return null;
                }, error -> {
                    //TODO Handle ELK error
                    DomGlobal.console.error("Error while performing layout: " + error);
                    resolve.onInvoke(parentNode);
                    return null;
                }));
    }

    @SuppressWarnings("all")
    public static ELKNode buildElkInputNode(Graph graph,
                                            Node parentNode,
                                            Object parentLayoutOptions,
                                            Object nestedParentLayoutOptions,
                                            boolean isSubset) {
        final String rootUUID = parentNode.getUUID();
        //ELK root node definition
        final ELKNode[] elkRoot = new ELKNode[]{new ELKNode(rootUUID, parentLayoutOptions)};

        final Map<String, String> nodeContainment = new HashMap<>();

        new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl())
                .setRootUUID(rootUUID)
                .traverse(graph, new ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

                    private Map<String, Boolean> processedNodes = new HashMap<>();

                    @Override
                    public boolean startNodeTraversal(List<Node<View, Edge>> parents, Node<View, Edge> node) {
                        final Node parent = parents.get(parents.size() - 1);

                        if (parent.getUUID().equals(rootUUID)) {
                            addNode(node);
                        } else {
                            //Containment
                            addChildNode(parent, node, nestedParentLayoutOptions);
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
                                              final Node<View, Edge> node,
                                              final Object nestedParentLayoutOptions) {
                        //skip in case the node was already processed
                        if (processedNodes.containsKey(node.getUUID()) || parent.getUUID().equals(rootUUID)) {
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
                        elkParent.setLayoutOptions(nestedParentLayoutOptions);

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
                if (edge.getSourceNode() == null || edge.getTargetNode() == null) {
                    return;
                }

                final ELKEdge elkEdge = new ELKEdge(edge.getUUID(),
                                                    edge.getSourceNode().getUUID(),
                                                    edge.getTargetNode().getUUID());
                String sourceParent = nodeContainment.get(edge.getSourceNode().getUUID());
                String targetParent = nodeContainment.get(edge.getTargetNode().getUUID());
                final Class<?> type = ((View<?>) edge.getContent()).getDefinition().getClass();

                // Transition priority in the same level
                if (EventConditionTransition.class.equals(type)) {
                    elkEdge.setPriority(0);
                } else if (ErrorTransition.class.equals(type)) {
                    elkEdge.setPriority(1);
                } else if (StartTransition.class.equals(type)) {
                    elkEdge.setPriority(2);
                } else if (Transition.class.equals(type)) {
                    elkEdge.setPriority(3);
                } else if (CompensationTransition.class.equals(type)) {
                    elkEdge.setPriority(4);
                } else if (DefaultConditionTransition.class.equals(type)) {
                    elkEdge.setPriority(5);
                }

                //if nodes have same parent add edge into parent node structure
                if (null != sourceParent && sourceParent.equals(targetParent)) {
                    if (isSubset) {
                        elkRoot[0].getChild(sourceParent).addEdgeWithFilter(elkEdge);
                    } else {
                        elkRoot[0].getChild(sourceParent).addEdge(elkEdge);
                    }
                } else {
                    if (isSubset) {
                        elkRoot[0].addEdgeWithFilter(elkEdge);
                    } else {
                        elkRoot[0].addEdge(elkEdge);
                    }
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

        //add container into new root
        if (isSubset) {
            final ELKNode newElkRoot = new ELKNode("root", parentLayoutOptions);
            newElkRoot.addNode(elkRoot[0].setLayoutOptions(nestedParentLayoutOptions));

            //DomGlobal.console.log("----->" + Global.JSON.stringify(newElkRoot));

            return newElkRoot;
        }

        //DomGlobal.console.log("----->" + Global.JSON.stringify(elkRoot[0]));

        return elkRoot[0];
    }

    //Update node sizes after ELK processing
    @SuppressWarnings("all")
    public static void updateGraphNodeSizes(ELKNode elkRoot, Graph graph) {
        for (ELKNode elkNode : elkRoot.getChildren().asList()) {
            ((Node<View, Edge>) graph.getNode(elkNode.getId())).getContent()
                    .setBounds(Bounds.create(elkNode.getX(),
                                             elkNode.getY(),
                                             elkNode.getX() + elkNode.getWidth(),
                                             elkNode.getY() + elkNode.getHeight()));

            if (elkNode.getChildren().length > 0) {
                updateGraphNodeSizes(elkNode, graph);
            }
        }
    }

    @SuppressWarnings("all")
    public static void updateNodesPosition(ELKNode elkRoot,
                                           Graph graph,
                                           CompositeCommand.Builder layoutCommands) {
        for (ELKNode elkNode : elkRoot.getChildren().asList()) {
            layoutCommands.addCommand(new UpdateElementPositionCommand(graph.getNode(elkNode.getId()),
                                                                       new Point2D(elkNode.getX(), elkNode.getY())));
            if (elkNode.getChildren().length > 0) {
                updateNodesPosition(elkNode, graph, layoutCommands);
            }
        }
    }

    @SuppressWarnings("all")
    public static void createControlPoints(final ELKNode elkNode,
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
