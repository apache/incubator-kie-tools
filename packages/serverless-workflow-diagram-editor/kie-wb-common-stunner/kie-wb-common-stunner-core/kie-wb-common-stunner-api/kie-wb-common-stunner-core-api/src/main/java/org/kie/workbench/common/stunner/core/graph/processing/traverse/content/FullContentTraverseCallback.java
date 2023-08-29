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
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public interface FullContentTraverseCallback<N extends Node<View, Edge>, E extends Edge<Object, Node>>
        extends ContentTraverseCallback<Object, N, E> {

    void startViewEdgeTraversal(final E edge);

    void endViewEdgeTraversal(final E edge);

    void startChildEdgeTraversal(final E edge);

    void endChildEdgeTraversal(final E edge);

    void startParentEdgeTraversal(final E edge);

    void endParentEdgeTraversal(final E edge);
}
