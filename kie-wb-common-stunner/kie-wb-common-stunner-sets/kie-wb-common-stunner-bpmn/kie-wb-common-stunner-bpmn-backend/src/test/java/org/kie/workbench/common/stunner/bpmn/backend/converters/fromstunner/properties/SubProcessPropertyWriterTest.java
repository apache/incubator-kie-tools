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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.SubProcess;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseAdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubProcessPropertyWriterTest extends AbstractBasePropertyWriterTest<SubProcessPropertyWriter, SubProcess> {

    @Override
    protected SubProcessPropertyWriter newPropertyWriter(SubProcess baseElement, VariableScope variableScope) {
        return new SubProcessPropertyWriter(baseElement, variableScope);
    }

    @Override
    protected SubProcess mockElement() {
        return mock(SubProcess.class);
    }

    @Test
    public void addChildElement() {
        SubProcess process = (SubProcess) propertyWriter.getElement();
        List<FlowElement> flowElements = new ArrayList<>();
        when(process.getFlowElements()).thenReturn(flowElements);

        BoundaryEventPropertyWriter boundaryEventPropertyWriter =
                new BoundaryEventPropertyWriter(bpmn2.createBoundaryEvent(), variableScope);

        UserTaskPropertyWriter userTaskPropertyWriter =
                new UserTaskPropertyWriter(bpmn2.createUserTask(), variableScope);

        propertyWriter.addChildElement(boundaryEventPropertyWriter);
        propertyWriter.addChildElement(userTaskPropertyWriter);

        // boundary event should always occur after other nodes (compat with old marshallers)
        assertThat(process.getFlowElements().get(0)).isEqualTo(userTaskPropertyWriter.getFlowElement());
        assertThat(process.getFlowElements().get(1)).isEqualTo(boundaryEventPropertyWriter.getFlowElement());
    }

    @Test
    public void testSetAbsoluteBoundsForAdHocSubprocess() {
        testSetAbsoluteBoundsForExpandedNode(createNode(new BaseAdHocSubprocessMock()));
    }

    @Test
    public void testSetAbsoluteBoundsForEmbeddedSubprocess() {
        testSetAbsoluteBoundsForExpandedNode(createNode(mock(EmbeddedSubprocess.class)));
    }

    @Test
    public void testSetAbsoluteBoundsForEventSubprocess() {
        testSetAbsoluteBoundsForExpandedNode(createNode(mock(EventSubprocess.class)));
    }

    private void testSetAbsoluteBoundsForExpandedNode(Node<View, ?> node) {
        testSetAbsoluteBounds(node);
        assertTrue(propertyWriter.getShape().isIsExpanded());
    }

    private class BaseAdHocSubprocessMock extends BaseAdHocSubprocess {

        BaseAdHocSubprocessMock() {
            this(null, null, null, null, null);
        }

        private BaseAdHocSubprocessMock(BPMNGeneralSet general, BackgroundSet backgroundSet, FontSet fontSet, RectangleDimensionsSet dimensionsSet, SimulationSet simulationSet) {
            super(general, backgroundSet, fontSet, dimensionsSet, simulationSet);
        }

        @Override
        public BaseAdHocSubprocessTaskExecutionSet getExecutionSet() {
            return null;
        }

        @Override
        public void setExecutionSet(BaseAdHocSubprocessTaskExecutionSet executionSet) {

        }

        @Override
        public BaseProcessData getProcessData() {
            return null;
        }

        @Override
        public void setProcessData(BaseProcessData processData) {

        }
    }
}