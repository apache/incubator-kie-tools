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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractContentTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;

public class DrawCanvasCommand extends AbstractCanvasCommand {

    private final ChildrenTraverseProcessor childrenTraverseProcessor;
    private final ViewTraverseProcessor viewTraverseProcessor;

    public DrawCanvasCommand(final ChildrenTraverseProcessor childrenTraverseProcessor,
                             final ViewTraverseProcessor viewTraverseProcessor) {
        this.childrenTraverseProcessor = childrenTraverseProcessor;
        this.viewTraverseProcessor = viewTraverseProcessor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final Graph graph = context.getGraphIndex().getGraph();
        final String shapeSetId = getShapeSetId(context);

        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder =
                new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>().forward();

        // Aggregate all nodes in the parent-child-dock hierarchy.
        childrenTraverseProcessor
                .traverse(graph,
                          new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
                              private Map<String, Boolean> processedNodes = new HashMap<>();

                              @Override
                              public void startNodeTraversal(final Node<View, Edge> node) {
                                  super.startNodeTraversal(node);
                                  if (!isCanvasRoot().test(context,
                                                           node.getUUID())) {
                                      addNode(node);
                                  }
                              }

                              @Override
                              public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                                                final Node<View, Edge> node) {
                                  super.startNodeTraversal(parents,
                                                           node);
                                  final Optional<Edge> dockEdge = node.getInEdges().stream()
                                          .filter(e -> e.getContent() instanceof Dock)
                                          .findAny();
                                  final Node parent = dockEdge.map(Edge::getSourceNode)
                                          .orElseGet(() -> parents.get(parents.size() - 1));
                                  if (dockEdge.isPresent()) {
                                      addDockedNode(parent,
                                                    node);
                                  } else if (isCanvasRoot().
                                          test(context,
                                               parent.getUUID())) {
                                      addNode(node);
                                  } else {
                                      addChildNode(parent,
                                                   node);
                                  }
                                  return true;
                              }

                              private void addNode(final Node node) {
                                  //skip in case the node was already processed
                                  if (processedNodes.containsKey(node.getUUID())) {
                                      return;
                                  }

                                  commandBuilder.addCommand(new AddCanvasNodeCommand(node,
                                                                                     shapeSetId));
                                  addProcessedNode(node);
                              }

                              private void addChildNode(final Node<View, Edge> parent,
                                                        final Node<View, Edge> node) {
                                  //skip in case the node was already processed
                                  if (processedNodes.containsKey(node.getUUID())) {
                                      return;
                                  }

                                  //check whether the parent was processed, is must be processed before child node
                                  if (!processedNodes.containsKey(parent.getUUID())) {
                                      addNode(parent);
                                  }

                                  commandBuilder.addCommand(new AddCanvasChildNodeCommand(parent,
                                                                                          node,
                                                                                          shapeSetId));
                                  addProcessedNode(node);
                              }

                              private void addDockedNode(final Node<View, Edge> parent,
                                                         final Node<View, Edge> node) {
                                  //check whether the dock parent was processed, is must be processed before docked the node
                                  if (!processedNodes.containsKey(parent.getUUID())) {
                                      addNode(parent);
                                  }

                                  commandBuilder.addCommand(new AddCanvasDockedNodeCommand(parent,
                                                                                           node,
                                                                                           shapeSetId));
                                  addProcessedNode(node);
                              }

                              private void addProcessedNode(Node<View, Edge> node) {
                                  processedNodes.put(node.getUUID(), true);
                              }

                              @Override
                              public void endGraphTraversal() {
                                  super.endGraphTraversal();
                                  processedNodes.clear();
                                  processedNodes = null;
                              }
                          });

        // Aggregate all connectors.
        viewTraverseProcessor
                .traverse(graph,
                          new AbstractContentTraverseCallback<View<?>, Node<View, Edge>, Edge<View<?>, Node>>() {

                              @Override
                              public void startEdgeTraversal(final Edge<View<?>, Node> edge) {
                                  super.startEdgeTraversal(edge);
                                  commandBuilder.addCommand(new AddCanvasConnectorCommand(edge,
                                                                                          shapeSetId));
                              }
                          });

        return executeCommands(context, commandBuilder);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        throw new UnsupportedOperationException("Undo operation for [" + this.getClass().getName() + "[ is not supported..");
    }

    private BiPredicate<AbstractCanvasHandler, String> isCanvasRoot() {
        return (handler, uuid) -> getCanvasRootUUID(handler)
                .map(s -> s.equals(uuid))
                .orElse(false);
    }

    private Optional<String> getCanvasRootUUID(final AbstractCanvasHandler context) {
        return Optional.ofNullable(context.getDiagram().getMetadata().getCanvasRootUUID());
    }

    private String getShapeSetId(final AbstractCanvasHandler context) {
        return context.getDiagram().getMetadata().getShapeSetId();
    }

    protected CommandResult<CanvasViolation> executeCommands(AbstractCanvasHandler context,
                                                             CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        return commandBuilder
                .build()
                .execute(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
