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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

/**
 * Defines methods to perform helper actions in the graph to process the automatic layout.
 */
public interface GraphProcessor {

    /**
     * Extract the nodes from the graph to be used for automatic layout.
     *
     * @param graph The graph to perform automatic layout.
     * @return The nodes to be considered to automatic layout.
     */
    Iterable<? extends Node> getNodes(final Graph<?, ?> graph);

    /**
     * Checks if some existing node in the graph is replaced for another one to perform automatic layout.
     * For example, a node inside another node.
     *
     * @param uuid The uuid of the node.
     * @return True if the node is replaced by another one, false if it is not.
     */
    default boolean isReplacedByAnotherNode(final String uuid) {
        return false;
    }

    /**
     * Gets the UUID of the node to be considered instead for some specific node.
     *
     * @param uuid The UUID of the specific node.
     * @return The new UUID to be considered to perform automatic layout, if `isReplacedByAnotherNode(uuiid)` is false, it returns `uuid`.
     */
    default String getReplaceNodeId(final String uuid) {
        return uuid;
    }

    /**
     * Gets the replaced nodes collection, if there is any.
     * A node is replaced by another node if it is inside another node like a Decision inside a DecisionService.
     * In that case the node considered for layout is the parent node (DecisionService) and not the inner node (Decision).
     *
     * @return The collection of nodes replaced by another node.
     */
    default Map<String, String> getReplacedNodes() {
        return Collections.emptyMap();
    }

    /**
     * Connects an inner node with its parent node. This is used in diagrams like DMN for example when a node should
     * be positioned inside another node like a Decision node inside a DecisionService.
     *
     * @param parentNode The parent node.
     * @param innerNode  The inner (child) node.
     */
    default void connect(final Node parentNode, final Node innerNode) {
        // Does nothing because the graph (diagram) does not have inner nodes.
    }

    /**
     * Gets the position of an inner node inside a parent node.
     *
     * @param parentId          The ID of the parent node.
     * @param innerNodeId       The ID of the inner node.
     * @param horizontalPadding The horizontal padding for the inner node.
     * @param graph             The source graph of the nodes.
     * @return The VertexPosition of the inner node.
     */
    default VertexPosition getChildVertexPosition(final String parentId,
                                                  final String innerNodeId,
                                                  final double horizontalPadding,
                                                  final Graph<?, ?> graph) {
        return new VertexPosition() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public Point2D getUpperLeft() {
                return null;
            }

            @Override
            public Point2D getBottomRight() {
                return null;
            }
        };
    }

    /**
     * Get a node from a graph.
     * @param uuid The uuid of the node.
     * @param graph The graph.
     * @return The node of the graph.
     */
    default Optional<Node> getNodeFromGraph(final String uuid,
                                            final Graph<?, ?> graph) {
        for (final Node n : graph.nodes()) {
            if (n.getUUID() == uuid) {
                return Optional.of(n);
            }
        }
        return Optional.empty();
    }
}
