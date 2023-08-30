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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.activities;

import org.eclipse.bpmn2.CallActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.CallActivityPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CallActivityConverterTest {

    private static final String UUID = "UUID";
    private static final String NAME = "NAME";
    private static final String DOCUMENTATION = "DOCUMENTATION";
    private static final String CALLED_ELEMENT = "CALLED_ELEMENT";
    private static final Boolean INDEPENDENT = Boolean.TRUE;
    private static final Boolean ABORT_PARENT = Boolean.TRUE;
    private static final Boolean WAIT_FOR_COMPLETION = Boolean.TRUE;
    private static final Boolean IS_ASYNC = Boolean.TRUE;
    private static final Boolean SEQUENTIAL = Boolean.TRUE;
    private static final String COLLECTION_INPUT = "COLLECTION_INPUT";
    private static final String COLLECTION_OUTPUT = "COLLECTION_OUTPUT";
    private static final String DATA_INPUT = "DATA_INPUT";
    private static final String DATA_OUTPUT = "DATA_OUTPUT";
    private static final String COMPLETION_CONDITION = "COMPLETION_CONDITION";
    private static final ScriptTypeListValue ON_ENTRY_ACTION = new ScriptTypeListValue();
    private static final ScriptTypeListValue ON_EXIT_ACTION = new ScriptTypeListValue();
    private static final Bounds BOUNDS = Bounds.create();
    private static final SimulationSet SIMULATION_SET = new SimulationSet();
    private static final RectangleDimensionsSet RECTANGLE_DIMENSIONS_SET = new RectangleDimensionsSet();
    private static final FontSet FONT_SET = new FontSet();
    private static final BackgroundSet BACKGROUND_SET = new BackgroundSet();
    private static final AssignmentsInfo ASSIGNMENTS_INFO = new AssignmentsInfo();
    private static final String SLA_DUE_DATE = "12/25/1983";

    @Mock
    private TypedFactoryManager factoryManager;

    @Mock
    private PropertyReaderFactory propertyReaderFactory;

    @Mock
    private CallActivityPropertyReader propertyReader;

    @Mock
    private CallActivity callActivity;

    private CallActivityConverter converter;

    @Before
    public void setUp() {
        final ReusableSubprocess definition = new ReusableSubprocess(new BPMNGeneralSet(),
                                                                     new ReusableSubprocessTaskExecutionSet(),
                                                                     new DataIOSet(),
                                                                     new BackgroundSet(),
                                                                     new FontSet(),
                                                                     new RectangleDimensionsSet(),
                                                                     new SimulationSet(),
                                                                     new AdvancedData());
        final View<ReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        Node<View<ReusableSubprocess>, Edge> node = new NodeImpl<>(UUID);
        node.setContent(view);

        when(callActivity.getId()).thenReturn(UUID);
        when(callActivity.getCalledElement()).thenReturn(CALLED_ELEMENT);
        when(factoryManager.newNode(eq(UUID), eq(ReusableSubprocess.class))).thenReturn(node);
        when(propertyReaderFactory.of(callActivity)).thenReturn(propertyReader);

        when(propertyReader.getName()).thenReturn(NAME);
        when(propertyReader.getDocumentation()).thenReturn(DOCUMENTATION);
        when(propertyReader.getAssignmentsInfo()).thenReturn(ASSIGNMENTS_INFO);
        when(propertyReader.getBounds()).thenReturn(BOUNDS);
        when(propertyReader.getSimulationSet()).thenReturn(SIMULATION_SET);
        when(propertyReader.getRectangleDimensionsSet()).thenReturn(RECTANGLE_DIMENSIONS_SET);
        when(propertyReader.getFontSet()).thenReturn(FONT_SET);
        when(propertyReader.getBackgroundSet()).thenReturn(BACKGROUND_SET);
        when(propertyReader.isIndependent()).thenReturn(INDEPENDENT);
        when(propertyReader.isAbortParent()).thenReturn(ABORT_PARENT);
        when(propertyReader.isWaitForCompletion()).thenReturn(WAIT_FOR_COMPLETION);
        when(propertyReader.isAsync()).thenReturn(IS_ASYNC);
        when(propertyReader.isSequential()).thenReturn(SEQUENTIAL);
        when(propertyReader.getCollectionOutput()).thenReturn(COLLECTION_OUTPUT);
        when(propertyReader.getCollectionInput()).thenReturn(COLLECTION_INPUT);
        when(propertyReader.getDataInput()).thenReturn(DATA_INPUT);
        when(propertyReader.getDataOutput()).thenReturn(DATA_OUTPUT);
        when(propertyReader.getCompletionCondition()).thenReturn(COMPLETION_CONDITION);
        when(propertyReader.getOnEntryAction()).thenReturn(ON_ENTRY_ACTION);
        when(propertyReader.getOnExitAction()).thenReturn(ON_EXIT_ACTION);
        when(propertyReader.getSlaDueDate()).thenReturn(SLA_DUE_DATE);

        converter = new CallActivityConverter(factoryManager, propertyReaderFactory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConvertMI() {
        when(propertyReader.isMultipleInstance()).thenReturn(true);
        BpmnNode bpmnNode = converter.convert(callActivity).value();
        Node<View<ReusableSubprocess>, Edge> result = (Node<View<ReusableSubprocess>, Edge>) bpmnNode.value();
        assertCommonValues(result);

        assertTrue(result.getContent().getDefinition().getExecutionSet().getIsMultipleInstance().getValue());
        assertEquals(SEQUENTIAL, result.getContent().getDefinition().getExecutionSet().getMultipleInstanceExecutionMode().isSequential());
        assertEquals(COLLECTION_INPUT, result.getContent().getDefinition().getExecutionSet().getMultipleInstanceCollectionInput().getValue());
        assertEquals(COLLECTION_OUTPUT, result.getContent().getDefinition().getExecutionSet().getMultipleInstanceCollectionOutput().getValue());
        assertEquals(DATA_INPUT, result.getContent().getDefinition().getExecutionSet().getMultipleInstanceDataInput().getValue());
        assertEquals(DATA_OUTPUT, result.getContent().getDefinition().getExecutionSet().getMultipleInstanceDataOutput().getValue());
        assertEquals(COMPLETION_CONDITION, result.getContent().getDefinition().getExecutionSet().getMultipleInstanceCompletionCondition().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConvertNonMI() {
        when(propertyReader.isMultipleInstance()).thenReturn(false);
        when(propertyReader.isSequential()).thenReturn(false);
        when(propertyReader.getCollectionOutput()).thenReturn(null);
        when(propertyReader.getCollectionInput()).thenReturn(null);
        when(propertyReader.getDataInput()).thenReturn(null);
        when(propertyReader.getDataOutput()).thenReturn(null);
        when(propertyReader.getCompletionCondition()).thenReturn(null);

        BpmnNode bpmnNode = converter.convert(callActivity).value();
        Node<View<ReusableSubprocess>, Edge> result = (Node<View<ReusableSubprocess>, Edge>) bpmnNode.value();
        assertCommonValues(result);

        assertEquals(false, result.getContent().getDefinition().getExecutionSet().getIsMultipleInstance().getValue());
        assertFalse(result.getContent().getDefinition().getExecutionSet().getMultipleInstanceExecutionMode().isSequential());
        assertNull(result.getContent().getDefinition().getExecutionSet().getMultipleInstanceCollectionInput().getValue());
        assertNull(result.getContent().getDefinition().getExecutionSet().getMultipleInstanceCollectionOutput().getValue());
        assertNull(result.getContent().getDefinition().getExecutionSet().getMultipleInstanceDataInput().getValue());
        assertNull(result.getContent().getDefinition().getExecutionSet().getMultipleInstanceDataOutput().getValue());
        assertNull(result.getContent().getDefinition().getExecutionSet().getMultipleInstanceCompletionCondition().getValue());
    }

    private void assertCommonValues(Node<View<ReusableSubprocess>, Edge> result) {
        assertEquals(UUID, result.getUUID());
        assertEquals(NAME, result.getContent().getDefinition().getGeneral().getName().getValue());
        assertEquals(DOCUMENTATION, result.getContent().getDefinition().getGeneral().getDocumentation().getValue());
        assertEquals(ASSIGNMENTS_INFO, result.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
        assertEquals(BOUNDS, result.getContent().getBounds());
        assertEquals(SIMULATION_SET, result.getContent().getDefinition().getSimulationSet());
        assertEquals(RECTANGLE_DIMENSIONS_SET, result.getContent().getDefinition().getDimensionsSet());
        assertEquals(FONT_SET, result.getContent().getDefinition().getFontSet());
        assertEquals(BACKGROUND_SET, result.getContent().getDefinition().getBackgroundSet());
        assertEquals(CALLED_ELEMENT, result.getContent().getDefinition().getExecutionSet().getCalledElement().getValue());
        assertEquals(INDEPENDENT, result.getContent().getDefinition().getExecutionSet().getIndependent().getValue());
        assertEquals(ABORT_PARENT, result.getContent().getDefinition().getExecutionSet().getAbortParent().getValue());
        assertEquals(WAIT_FOR_COMPLETION, result.getContent().getDefinition().getExecutionSet().getWaitForCompletion().getValue());
        assertEquals(IS_ASYNC, result.getContent().getDefinition().getExecutionSet().getIsAsync().getValue());
        assertEquals(ON_ENTRY_ACTION, result.getContent().getDefinition().getExecutionSet().getOnEntryAction().getValue());
        assertEquals(ON_EXIT_ACTION, result.getContent().getDefinition().getExecutionSet().getOnExitAction().getValue());
        assertEquals(SLA_DUE_DATE, result.getContent().getDefinition().getExecutionSet().getSlaDueDate().getValue());
    }
}
