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


package org.kie.workbench.common.stunner.core.graph.processing.traverse.tree;

import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * <p>Traverses over all elements in the graph by moving to adjacent nodes/vertices, no matter their content.</p>
 */
public interface TreeWalkTraverseProcessor extends TreeTraverseProcessor<Graph, Node, Edge> {

    TreeWalkTraverseProcessor useStartNodePredicate(final Predicate<Node<?, Edge>> predicate);

    void traverse(final Graph graph,
                  final Node root,
                  final TreeTraverseCallback<Graph, Node, Edge> callback);
}
