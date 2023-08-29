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


package org.kie.workbench.common.stunner.sw.client.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasDockedNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.ResizeNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractContentTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

// TODO: Refactor / merge this with DrawCanvasCommand.
// TODO: Others:
// - Restrict drag constraints to parent
// - Handle properly commands & undo/redos
public class DrawParentNodeCommand extends AbstractCanvasCommand {

    private final ChildrenTraverseProcessor childrenTraverseProcessor;
    private final ViewTraverseProcessor viewTraverseProcessor;

    public DrawParentNodeCommand() {
        this.childrenTraverseProcessor = new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl());
        this.viewTraverseProcessor = new ViewTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl());
    }

    public DrawParentNodeCommand(final ChildrenTraverseProcessor childrenTraverseProcessor,
                                 final ViewTraverseProcessor viewTraverseProcessor) {
        this.childrenTraverseProcessor = childrenTraverseProcessor;
        this.viewTraverseProcessor = viewTraverseProcessor;
    }

    public String rootUUID;

    @Override
    @SuppressWarnings("all")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final Graph graph = context.getGraphIndex().getGraph();
        final String shapeSetId = getShapeSetId(context);

        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder =
                new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>().forward();

        final Set<String> nodes = new HashSet<>();

        // Aggregate all nodes in the parent-child-dock hierarchy.
        childrenTraverseProcessor
                .setRootUUID(rootUUID)
                .traverse(graph,
                          new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
                              private Map<String, Boolean> processedNodes = new HashMap<>();

                              @Override
                              public void startNodeTraversal(final Node<View, Edge> node) {
                                  super.startNodeTraversal(node);
                                  if (!isRootNode(node.getUUID())) {
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
                                  } else if (isRootNode(parent.getUUID())) {
                                      addNode(node);
                                  } else {
                                      addChildNode(parent,
                                                   node);
                                  }
                                  return true;
                              }

                              private void addNode(final Node node) {
                                  //Calculated parent size
                                  final Bounds bounds = ((View) node.getContent()).getBounds();

                                  //skip in case the node was already processed
                                  if (processedNodes.containsKey(node.getUUID())) {
                                      return;
                                  }

                                  commandBuilder.addCommand(new AddCanvasNodeCommand(node,
                                                                                     shapeSetId));

                                  commandBuilder.addCommand(new ResizeNodeCommand(node,
                                                                                  new BoundingBox(bounds.getX(),
                                                                                                  bounds.getY(),
                                                                                                  bounds.getWidth(),
                                                                                                  bounds.getHeight()),
                                                                                  (shape, point) -> {
                                                                                      return null;
                                                                                  }));
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
                                  nodes.addAll(processedNodes.keySet());
                                  processedNodes.clear();
                                  processedNodes = null;
                              }
                          });

        final Predicate<Node> isNodeProcessed = n -> null != n && nodes.contains(n.getUUID());

        // Aggregate all connectors.
        viewTraverseProcessor
                .traverse(graph,
                          new AbstractContentTraverseCallback<View<?>, Node<View, Edge>, Edge<View<?>, Node>>() {

                              @Override
                              public void startEdgeTraversal(final Edge<View<?>, Node> edge) {
                                  super.startEdgeTraversal(edge);
                                  if (isNodeProcessed.test(edge.getSourceNode()) &&
                                          isNodeProcessed.test(edge.getTargetNode())) {
                                      commandBuilder.addCommand(new AddCanvasConnectorCommand(edge,
                                                                                              shapeSetId));
                                  }
                              }
                          });

        return executeCommands(context, commandBuilder);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        throw new UnsupportedOperationException("Undo operation for [" + this.getClass().getName() + "[ is not supported..");
    }

    private boolean isRootNode(String uuid) {
        // return rootUUID.equals(uuid);
        return false;
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