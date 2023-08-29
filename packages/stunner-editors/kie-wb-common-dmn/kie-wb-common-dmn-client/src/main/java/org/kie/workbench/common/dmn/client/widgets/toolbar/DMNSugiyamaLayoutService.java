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

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.graph.DMNSizeHandler;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.SizeHandler;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.VertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning;

@DMNEditor
public class DMNSugiyamaLayoutService extends SugiyamaLayoutService implements LayoutService {

    /**
     * Default constructor.
     *
     * @param cycleBreaker      The strategy used to break cycles in cycle graphs.
     * @param vertexLayerer     The strategy used to choose the layer for each vertex.
     * @param vertexOrdering    The strategy used to order vertices inside each layer.
     * @param vertexPositioning The strategy used to position vertices on screen (x,y coordinates).
     * @param graphProcessor    Applies some pre-process in the graph to extract the nodes to be used.
     */
    @Inject
    public DMNSugiyamaLayoutService(final CycleBreaker cycleBreaker,
                                    final VertexLayerer vertexLayerer,
                                    final VertexOrdering vertexOrdering,
                                    final VertexPositioning vertexPositioning,
                                    final @Any DMNGraphProcessor graphProcessor) {
        super(cycleBreaker, vertexLayerer, vertexOrdering, vertexPositioning, graphProcessor);
    }

    @Override
    public SizeHandler getSizeHandler() {
        return new DMNSizeHandler();
    }
}
