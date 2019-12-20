/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.tests;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.resources.xml.UnmarshallerXMLTests;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class InputDataTest extends BaseDMNTest {

    private static final String INPUT_DATA_UUID = "_F60D7991-B11F-4760-87B7-4F105E52C611";

    private static final String INPUT_DATA_NAME = "InputData-1";

    private static final String INPUT_DATA_VARIABLE_UUID = "_CCCEBA75-272F-4EAE-9DDF-C1C923E62B1D";

    @Override
    public String getTestName() {
        return getClass().getName();
    }

    @Override
    public void run(final KogitoClientDiagramService service) throws AssertionError {
        test(service,
             UnmarshallerXMLTests.INSTANCE.inputData().getText());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doAssertions(final Diagram diagram) throws AssertionError {
        assertNotNull(diagram);

        final Node<Definition<DMNDiagram>, ?> dmnDiagramNode = GraphUtils.getFirstNode(diagram.getGraph(), DMNDiagram.class);
        assertNotNull("DMNDiagram node is not null", dmnDiagramNode);
        assertEquals("DMNDiagram has one outgoing edge", 1, dmnDiagramNode.getOutEdges().size());

        final Node<Definition<InputData>, ?> dmnInputDataNode = GraphUtils.getFirstNode(diagram.getGraph(), InputData.class);
        assertNotNull("InputData node is not null", dmnInputDataNode);
        assertEquals("InputData has one incoming edge", 1, dmnInputDataNode.getInEdges().size());

        final InputData inputData = dmnInputDataNode.getContent().getDefinition();
        assertEquals("InputData UUID", INPUT_DATA_UUID, inputData.getId().getValue());
        assertEquals("InputData name", INPUT_DATA_NAME, inputData.getName().getValue());

        assertEquals("DMNDiagram is connected to InputData (source)", dmnDiagramNode.getUUID(), dmnDiagramNode.getOutEdges().get(0).getSourceNode().getUUID());
        assertEquals("DMNDiagram is connected to InputData (target)", dmnInputDataNode.getUUID(), dmnDiagramNode.getOutEdges().get(0).getTargetNode().getUUID());
        assertEquals("InputData is connected to DMNDiagram (source)", dmnDiagramNode.getUUID(), dmnInputDataNode.getInEdges().get(0).getSourceNode().getUUID());
        assertEquals("InputData is connected to DMNDiagram (target)", dmnInputDataNode.getUUID(), dmnInputDataNode.getInEdges().get(0).getTargetNode().getUUID());

        final IsInformationItem variable = inputData.getVariable();
        assertNotNull("InputData variable is not null", variable);
        assertEquals("InputData variable UUID", INPUT_DATA_VARIABLE_UUID, variable.getId().getValue());
        assertEquals("InputData variable name", "", variable.getName().getValue());

        assertEquals("InputData width (DimensionSet)", 100.0, inputData.getDimensionsSet().getWidth().getValue());
        assertEquals("InputData height (DimensionSet)", 50.0, inputData.getDimensionsSet().getHeight().getValue());
        assertEquals("InputData fill colour (BackgroundSet)", "#ffffff", inputData.getBackgroundSet().getBgColour().getValue());
        assertEquals("InputData border colour (BackgroundSet)", "#000000", inputData.getBackgroundSet().getBorderColour().getValue());
        assertEquals("InputData font colour (FontSet)", "#000000", inputData.getFontSet().getFontColour().getValue());

        final Definition<InputData> inputDataDefinition = dmnInputDataNode.getContent();
        assertTrue("InputData definition is instance of View", inputDataDefinition instanceof View);
        
        final View inputDataView = (View) inputDataDefinition;
        assertEquals("InputData width (View)", 100.0, inputDataView.getBounds().getWidth());
        assertEquals("InputData height (View)", 50.0, inputDataView.getBounds().getHeight());
        assertEquals("InputData x (View)", 252.0, inputDataView.getBounds().getX());
        assertEquals("InputData y (View)", 150.0, inputDataView.getBounds().getY());
    }
}
