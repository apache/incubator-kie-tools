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

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layout;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutExecutor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;

@Default
public class LayoutHelper {

    private final LayoutService layoutService;

    @Inject
    public LayoutHelper(final LayoutService layoutService) {
        this.layoutService = layoutService;
    }

    public void applyLayout(final Diagram diagram, final LayoutExecutor layoutExecutor) {
        applyLayout(diagram, layoutExecutor, false);
    }

    public void applyLayout(final Diagram diagram,
                            final LayoutExecutor layoutExecutor,
                            final boolean overrideCurrentLayout) {
        final Graph<?, Node> graph = diagram.getGraph();
        if (graph != null && (overrideCurrentLayout || !this.layoutService.hasLayoutInformation(graph))) {
            final Layout layout = this.layoutService.createLayout(graph);
            layoutExecutor.applyLayout(layout, graph, layoutService.getSizeHandler());

            for (final Node node : graph.nodes()) {
                if (CanvasLayoutUtils.isCanvasRoot(diagram, node)) {
                    if (node.getContent() instanceof HasBounds) {
                        ((HasBounds) node.getContent()).setBounds(Bounds.create(0, 0, 0, 0));
                    }
                }
            }
        }
    }

    public boolean hasLayoutInformation(final Diagram diagram) {
        return layoutService.hasLayoutInformation(diagram.getGraph());
    }

}
