/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.canvas.util;

import java.util.List;

import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlightVisitor;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.uberfire.mvp.Command;

public class CaseManagementCanvasHighlightVisitor extends CanvasHighlightVisitor {

    @Override
    @SuppressWarnings("unchecked")
    protected void prepareVisit(Command command) {
        final Graph graph = canvasHandler.getDiagram().getGraph();

        new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl())
                .traverse(graph,
                          new ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
                              @Override
                              public boolean startNodeTraversal(List<Node<View, Edge>> parents, Node<View, Edge> node) {
                                  addShape(node.getUUID());
                                  return true;
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
                                  addShape(node.getUUID());
                              }

                              @Override
                              public void endNodeTraversal(Node<View, Edge> node) {

                              }

                              @Override
                              public void endGraphTraversal() {
                                  command.execute();
                              }

                              private void addShape(final String uuid) {
                                  final Shape shape = canvasHandler.getCanvas().getShape(uuid);
                                  if (null != shape) {
                                      shapes.add(shape);
                                  }
                              }
                          });
    }

    List<Shape> getShapes() {
        return shapes;
    }
}
