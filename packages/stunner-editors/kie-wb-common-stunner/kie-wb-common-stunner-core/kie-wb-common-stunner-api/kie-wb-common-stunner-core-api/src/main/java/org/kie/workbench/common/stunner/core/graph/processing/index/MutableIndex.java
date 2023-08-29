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

package org.kie.workbench.common.stunner.core.graph.processing.index;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;

public interface MutableIndex<N extends Node, E extends Edge> extends Index<N, E> {

    /**
     * Adds a node into the given index.
     */
    MutableIndex<N, E> addNode(final N node);

    /**
     * Removes a node from the given index.
     */
    MutableIndex<N, E> removeNode(final N node);

    /**
     * Adds an edge into the given index.
     */
    MutableIndex<N, E> addEdge(final E edge);

    /**
     * Removes an edge from the given index.
     */
    MutableIndex<N, E> removeEdge(final E edge);

    /**
     * Clears an index.
     */
    void clear();
}
