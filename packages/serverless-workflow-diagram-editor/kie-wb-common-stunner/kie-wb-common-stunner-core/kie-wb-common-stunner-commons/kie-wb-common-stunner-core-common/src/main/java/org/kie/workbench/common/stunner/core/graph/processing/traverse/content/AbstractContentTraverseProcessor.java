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

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

public abstract class AbstractContentTraverseProcessor<C, N extends Node<View, Edge>, E extends Edge<C, Node>, K extends ContentTraverseCallback<C, N, E>>
        implements ContentTraverseProcessor<C, N, E, K> {

    TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    public AbstractContentTraverseProcessor(final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
    }

    @SuppressWarnings("unchecked")
    protected boolean doStartEdgeTraversal(final Edge edge,
                                           final K callback) {
        if (accepts(edge)) {
            callback.startEdgeTraversal((E) edge);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected boolean doEndEdgeTraversal(final Edge edge,
                                         final K callback) {
        if (accepts(edge)) {
            callback.endEdgeTraversal((E) edge);
            return true;
        }
        return false;
    }

    protected boolean accepts(final Edge edge) {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected void doStartGraphTraversal(final Graph graph,
                                         final K callback) {
        if (graph.getContent() instanceof View) {
            callback.startGraphTraversal(graph);
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean doStartNodeTraversal(final Node node,
                                           final K callback) {
        if (node.getContent() instanceof View) {
            callback.startNodeTraversal((N) node);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected void doEndNodeTraversal(final Node node,
                                      final K callback) {
        if (node.getContent() instanceof View) {
            callback.endNodeTraversal((N) node);
        }
    }

    protected void doEndGraphTraversal(final Graph graph,
                                       final K callback) {
        callback.endGraphTraversal();
    }

    @Override
    public void traverse(final Graph<View, N> graph,
                         final K callback) {
        treeWalkTraverseProcessor
                .traverse(graph,
                          new TreeTraverseCallback<Graph, Node, Edge>() {

                              @Override
                              public void startGraphTraversal(final Graph graph) {
                                  AbstractContentTraverseProcessor.this.doStartGraphTraversal(graph,
                                                                                              callback);
                              }

                              @Override
                              public boolean startNodeTraversal(final Node node) {
                                  return AbstractContentTraverseProcessor.this.doStartNodeTraversal(node,
                                                                                                    callback);
                              }

                              @Override
                              public boolean startEdgeTraversal(final Edge edge) {
                                  return AbstractContentTraverseProcessor.this.doStartEdgeTraversal(edge,
                                                                                                    callback);
                              }

                              @Override
                              public void endNodeTraversal(final Node node) {
                                  AbstractContentTraverseProcessor.this.doEndNodeTraversal(node,
                                                                                           callback);
                              }

                              @Override
                              public void endEdgeTraversal(final Edge edge) {
                                  AbstractContentTraverseProcessor.this.doEndEdgeTraversal(edge,
                                                                                           callback);
                              }

                              @Override
                              public void endGraphTraversal() {
                                  AbstractContentTraverseProcessor.this.doEndGraphTraversal(graph,
                                                                                            callback);
                              }
                          });
    }
}
