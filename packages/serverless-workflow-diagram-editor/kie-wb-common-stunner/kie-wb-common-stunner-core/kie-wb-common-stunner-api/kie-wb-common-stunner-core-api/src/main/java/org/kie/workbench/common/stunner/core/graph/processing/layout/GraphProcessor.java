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


package org.kie.workbench.common.stunner.core.graph.processing.layout;

import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * Defines methods to perform helper actions in the graph to process the automatic layout.
 */
public interface GraphProcessor {

    /**
     * Extract the nodes from the graph to be used for automatic layout.
     * @param graph The graph to perform automatic layout.
     * @return The nodes to be considered to automatic layout.
     */
    Iterable<? extends Node> getNodes(final Graph<?, ?> graph);

    /**
     * Checks if some existing node in the graph is replaced for another one to perform automatic layout.
     * For example, a node inside another node.
     * @param uuid The uuid of the node.
     * @return True if the node is replaced by another one, false if it is not.
     */
    default boolean isReplacedByAnotherNode(final String uuid) {
        return false;
    }

    /**
     * Gets the UUID of the node to be considered instead for some specific node.
     * @param uuid The UUID of the specific node.
     * @return The new UUID to be considered to perform automatic layout, if `isReplacedByAnotherNode(uuiid)` is false, it returns `uuid`.
     */
    default String getReplaceNodeId(final String uuid) {
        return uuid;
    }
}
