/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
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

        final CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation> commandBuilder =
                new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>().forward();

        // Aggregate all nodes in the parent-child-dock hierarchy.
        childrenTraverseProcessor
                .traverse(graph,
                          new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

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

                              private void addNode(final Node<View, Edge> node) {
                                  commandBuilder.addCommand(new AddCanvasNodeCommand(node,
                                                                                     shapeSetId));
                              }

                              private void addChildNode(final Node<View, Edge> parent,
                                                        final Node<View, Edge> node) {
                                  commandBuilder.addCommand(new AddCanvasChildNodeCommand(parent,
                                                                                          node,
                                                                                          shapeSetId));
                              }

                              private void addDockedNode(final Node<View, Edge> parent,
                                                         final Node<View, Edge> node) {
                                  commandBuilder.addCommand(new AddCanvasDockedNodeCommand(parent,
                                                                                           node,
                                                                                           shapeSetId));
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

        return commandBuilder
                .build()
                .execute(context);
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
}
