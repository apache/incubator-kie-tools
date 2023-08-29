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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;

public abstract class AbstractLayoutService implements LayoutService {

    private static final double CLOSE_TO_ZERO_TOLERANCE = 0.1;

    /**
     * Checks if the specified graph has layout information.
     * A graph with at 25% of its nodes or less at position (0,0) is considered a graph with no layout information.
     *
     * @param graph The graph.
     * @return True if the graph has layout information, false otherwise.
     */
    @Override
    public boolean hasLayoutInformation(final Graph<?, ?> graph) {

        final List<Node> nodes = new ArrayList<>();
        graph.nodes().iterator().forEachRemaining(nodes::add);

        if (nodes.size() <= 1) {
            return true;
        }

        final double threshold = getLayoutInformationThreshold(nodes);
        int nodesWithLayout = 0;

        for (final Node n : nodes) {

            final Object content = n.getContent();
            if (content instanceof HasBounds) {
                if (!isNullOrCloseToZero(((HasBounds) content).getBounds())) {
                    nodesWithLayout++;
                }
            }

            if (nodesWithLayout >= threshold) {
                return true;
            }
        }

        return false;
    }

    protected double getLayoutInformationThreshold(final List<Node> nodes) {
        return nodes.size() / 4.0D;
    }

    private static boolean isNullOrCloseToZero(final Bounds bounds) {

        if (bounds == null) {
            return true;
        }

        final Bound upperLeft = bounds.getUpperLeft();

        /* We're ignoring bottomRight because it used to define the size of the elements,
         * not the position on the diagram.*/
        return isCloseToZero(upperLeft.getX())
                && isCloseToZero(upperLeft.getY());
    }

    protected static boolean isCloseToZero(final double value) {
        return Math.abs(value - 0) < CLOSE_TO_ZERO_TOLERANCE;
    }

    @Override
    public abstract Layout createLayout(Graph<?, ?> graph);
}
