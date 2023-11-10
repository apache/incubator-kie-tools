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


package org.kie.workbench.common.stunner.core.graph.processing.traverse.content;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Parent;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

@Dependent
public class FullContentTraverseProcessorImpl implements FullContentTraverseProcessor {

    TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    @Inject
    public FullContentTraverseProcessorImpl(final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void traverse(final Graph<View, Node<View, Edge>> graph,
                         final FullContentTraverseCallback<Node<View, Edge>, Edge<Object, Node>> callback) {
        treeWalkTraverseProcessor
                .traverse(graph,
                          new TreeTraverseCallback<Graph, Node, Edge>() {

                              @Override
                              public void startGraphTraversal(final Graph graph) {
                                  if (graph.getContent() instanceof DefinitionSet) {
                                      callback.startGraphTraversal(graph);
                                  }
                              }

                              @Override
                              public boolean startNodeTraversal(final Node node) {
                                  if (node.getContent() instanceof View) {
                                      callback.startNodeTraversal(node);
                                      return true;
                                  }
                                  return false;
                              }

                              @Override
                              public boolean startEdgeTraversal(final Edge edge) {
                                  if (edge.getContent() instanceof View) {
                                      callback.startViewEdgeTraversal(edge);
                                  } else if (edge.getContent() instanceof Child) {
                                      callback.startChildEdgeTraversal(edge);
                                  } else if (edge.getContent() instanceof Parent) {
                                      callback.startParentEdgeTraversal(edge);
                                  } else {
                                      callback.startEdgeTraversal(edge);
                                  }
                                  return true;
                              }

                              @Override
                              public void endNodeTraversal(final Node node) {
                                  if (node.getContent() instanceof View) {
                                      callback.endNodeTraversal(node);
                                  }
                              }

                              @Override
                              public void endEdgeTraversal(final Edge edge) {
                                  if (edge.getContent() instanceof View) {
                                      callback.endViewEdgeTraversal(edge);
                                  } else if (edge.getContent() instanceof Child) {
                                      callback.endChildEdgeTraversal(edge);
                                  } else if (edge.getContent() instanceof Parent) {
                                      callback.endParentEdgeTraversal(edge);
                                  } else {
                                      callback.endEdgeTraversal(edge);
                                  }
                              }

                              @Override
                              public void endGraphTraversal() {
                                  callback.endGraphTraversal();
                              }
                          });
    }
}
