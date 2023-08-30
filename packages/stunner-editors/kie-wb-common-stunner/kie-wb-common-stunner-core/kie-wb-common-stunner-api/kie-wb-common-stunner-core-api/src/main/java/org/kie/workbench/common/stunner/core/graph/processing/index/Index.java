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
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * <p>A generic graph index based on element's identifiers. Allows performing fast look-ups over the graph elements.</p>
 */
public interface Index<N extends Node, E extends Edge> {

    /**
     * Return the graph instance which has been indexed by this index.
     */
    Graph<?, N> getGraph();

    /**
     * Returns the element (node or edge) with the given uuid.
     */
    Element get(final String uuid);

    /**
     * Returns the node with the given uuid.
     */
    N getNode(final String uuid);

    /**
     * Returns the edge with the given uuid.
     */
    E getEdge(final String uuid);
}
