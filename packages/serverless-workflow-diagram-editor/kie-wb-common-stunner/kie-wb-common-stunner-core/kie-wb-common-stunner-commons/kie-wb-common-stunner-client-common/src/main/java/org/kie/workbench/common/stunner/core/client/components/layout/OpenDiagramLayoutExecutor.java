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


package org.kie.workbench.common.stunner.core.client.components.layout;

import java.util.HashMap;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layout;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutExecutor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPosition;

/**
 * Copies the layout information to a recently opened diagram without layout information.
 */
@Default
@Dependent
public final class OpenDiagramLayoutExecutor implements LayoutExecutor {

    @Override
    public void applyLayout(final Layout layout,
                            final Graph graph) {
        if (layout.getNodePositions().size() == 0) {
            return;
        }

        final HashMap<String, Node> indexByUuid = new HashMap<>();
        for (final Object n : graph.nodes()) {

            if (n instanceof Node) {
                final Node node = (Node) n;
                indexByUuid.put(node.getUUID(), node);
            }
        }

        for (final VertexPosition position : layout.getNodePositions()) {

            final Node indexed = indexByUuid.get(position.getId());
            if (indexed.getContent() instanceof HasBounds) {
                ((HasBounds) indexed.getContent()).setBounds(Bounds.create(
                        position.getUpperLeft().getX(),
                        position.getUpperLeft().getY(),
                        position.getBottomRight().getX(),
                        position.getBottomRight().getY()
                ));
            }
        }
    }
}
