/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.nodes;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class ParallelGatewayTest extends GatewayNodeTest<ParallelGateway> {

    private static final String BPMN_PARALLEL_GATEWAY_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/parallelGateways.bpmn";

    private static final String FILLED_TOP_LEVEL_GATEWAY_ID = "5C6F1A12-3B5D-4558-8F24-3DDE41F24A0A";
    private static final String EMPTY_TOP_LEVEL_GATEWAY_ID = "162CA3E2-47D1-40D9-B4AF-D6FB26F02A38";
    private static final String FILLED_SUBPROCESS_LEVEL_GATEWAY_ID = "66BF84BC-D7DB-485B-8AF7-4B6B3D219531";
    private static final String EMPTY_SUBPROCESS_LEVEL_GATEWAY_ID = "3AA557EF-C3F8-4D73-98BB-7F96D3B97DDF";

    private static final String DOCUMENTATION = "Some documentation as well ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
    private static final String NAME = "Some name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
    private static final String EMPTY = "";

    public ParallelGatewayTest() {
    }

    @Test
    public void testUnmarshallTopLevelGatewayFilledProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_PARALLEL_GATEWAY_FILE_PATH);
        assertDiagram(diagram, DIAGRAM_NODE_SIZE);

        ParallelGateway filledTopLevelGatewayDiverging = getGatewayNodeById(diagram, FILLED_TOP_LEVEL_GATEWAY_ID, getGatewayNodeType());
        assertGeneralSet(filledTopLevelGatewayDiverging.getGeneral(), NAME, DOCUMENTATION);
    }

    @Test
    public void testUnmarshallTopLevelEmptyGatewayProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_PARALLEL_GATEWAY_FILE_PATH);
        assertDiagram(diagram, DIAGRAM_NODE_SIZE);

        ParallelGateway emptyTopDiv = getGatewayNodeById(diagram, EMPTY_TOP_LEVEL_GATEWAY_ID, getGatewayNodeType());
        assertGeneralSet(emptyTopDiv.getGeneral(), EMPTY, EMPTY);
    }

    @Test
    public void testUnmarshallSubprocessLevelGatewayFilledProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_PARALLEL_GATEWAY_FILE_PATH);
        assertDiagram(diagram, DIAGRAM_NODE_SIZE);

        ParallelGateway filledTopLevelGatewayDiverging = getGatewayNodeById(diagram, FILLED_SUBPROCESS_LEVEL_GATEWAY_ID, getGatewayNodeType());
        assertGeneralSet(filledTopLevelGatewayDiverging.getGeneral(), NAME, DOCUMENTATION);
    }

    @Test
    public void testUnmarshallSubprocessLevelGatewayEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_PARALLEL_GATEWAY_FILE_PATH);
        assertDiagram(diagram, DIAGRAM_NODE_SIZE);

        ParallelGateway filledTopLevelGatewayDiverging = getGatewayNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_GATEWAY_ID, getGatewayNodeType());
        assertGeneralSet(filledTopLevelGatewayDiverging.getGeneral(), EMPTY, EMPTY);
    }

    @Override
    Class<ParallelGateway> getGatewayNodeType() {
        return ParallelGateway.class;
    }

    @Override
    String getGatewayNodeFilePath() {
        return BPMN_PARALLEL_GATEWAY_FILE_PATH;
    }

    @Override
    String getFilledTopLevelGatewayId() {
        return FILLED_TOP_LEVEL_GATEWAY_ID;
    }

    @Override
    String getEmptyTopLevelGatewayId() {
        return EMPTY_TOP_LEVEL_GATEWAY_ID;
    }

    @Override
    String getFilledSubprocessLevelGatewayId() {
        return FILLED_SUBPROCESS_LEVEL_GATEWAY_ID;
    }

    @Override
    String getEmptySubprocessLevelGatewayId() {
        return EMPTY_SUBPROCESS_LEVEL_GATEWAY_ID;
    }
}
