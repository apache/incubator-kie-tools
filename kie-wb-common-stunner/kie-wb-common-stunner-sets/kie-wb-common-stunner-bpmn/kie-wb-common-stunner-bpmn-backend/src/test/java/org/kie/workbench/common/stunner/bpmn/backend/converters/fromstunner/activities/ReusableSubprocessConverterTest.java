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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.activities;

import org.eclipse.bpmn2.CallActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CallActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReusableSubprocessConverterTest {

    private static final String UUID = "UUID";
    private static final String NAME = "NAME";
    private static final String DOCUMENTATION = "DOCUMENTATION";
    private static final String CALLED_ELEMENT = "CALLED_ELEMENT";
    private static final Boolean INDEPENDENT = Boolean.TRUE;
    private static final Boolean IS_CASE = Boolean.FALSE;
    private static final Boolean WAIT_FOR_COMPLETION = Boolean.TRUE;
    private static final Boolean IS_ASYNC = Boolean.TRUE;
    private static final Boolean IS_ADHOC_AUTOSTART = Boolean.FALSE;
    private static final Boolean IS_MULTIPLE_INSTANCE = Boolean.TRUE;
    private static final Boolean SEQUENTIAL = Boolean.TRUE;
    private static final String COLLECTION_INPUT = "COLLECTION_INPUT";
    private static final String COLLECTION_OUTPUT = "COLLECTION_OUTPUT";
    private static final String DATA_INPUT = "DATA_INPUT";
    private static final String DATA_OUTPUT = "DATA_OUTPUT";
    private static final String COMPLETION_CONDITION = "COMPLETION_CONDITION";
    private static final OnEntryAction ON_ENTRY_ACTION = new OnEntryAction();
    private static final OnExitAction ON_EXIT_ACTION = new OnExitAction();
    private static final Bounds BOUNDS = Bounds.create();
    private static final SimulationSet SIMULATION_SET = new SimulationSet();
    private static final AssignmentsInfo ASSIGNMENTS_INFO = new AssignmentsInfo();

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    @Mock
    private CallActivityPropertyWriter propertyWriter;

    @Captor
    private ArgumentCaptor<CallActivity> activityCaptor;

    private Node<View<BaseReusableSubprocess>, ?> node;

    private ReusableSubprocessConverter converter;

    @Before
    public void setUp() {
        when(propertyWriterFactory.of(any(CallActivity.class))).thenReturn(propertyWriter);

        DataIOSet ioSet = mock(DataIOSet.class);
        when(ioSet.getAssignmentsinfo()).thenReturn(ASSIGNMENTS_INFO);
        final ReusableSubprocess definition = new ReusableSubprocess(new BPMNGeneralSet(NAME, DOCUMENTATION),
                                                                     new ReusableSubprocessTaskExecutionSet(new CalledElement(CALLED_ELEMENT),
                                                                                                            new IsCase(IS_CASE),
                                                                                                            new Independent(INDEPENDENT),
                                                                                                            new WaitForCompletion(WAIT_FOR_COMPLETION),
                                                                                                            new IsAsync(IS_ASYNC),
                                                                                                            new AdHocAutostart(IS_ADHOC_AUTOSTART),
                                                                                                            new IsMultipleInstance(IS_MULTIPLE_INSTANCE),
                                                                                                            new MultipleInstanceExecutionMode(SEQUENTIAL),
                                                                                                            new MultipleInstanceCollectionInput(COLLECTION_INPUT),
                                                                                                            new MultipleInstanceDataInput(DATA_INPUT),
                                                                                                            new MultipleInstanceCollectionOutput(COLLECTION_OUTPUT),
                                                                                                            new MultipleInstanceDataOutput(DATA_OUTPUT),
                                                                                                            new MultipleInstanceCompletionCondition(COMPLETION_CONDITION),
                                                                                                            ON_ENTRY_ACTION,
                                                                                                            ON_EXIT_ACTION),
                                                                     ioSet,
                                                                     new BackgroundSet(),
                                                                     new FontSet(),
                                                                     new RectangleDimensionsSet(),
                                                                     SIMULATION_SET);
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, BOUNDS);
        node = new NodeImpl<>(UUID);
        node.setContent(view);
        converter = new ReusableSubprocessConverter(propertyWriterFactory);
    }

    @Test
    public void testToFlowElementMI() {
        assertEquals(propertyWriter, converter.toFlowElement(node));
        verifyCommonValues();
        verify(propertyWriter).setIsSequential(SEQUENTIAL);
        verify(propertyWriter).setCollectionInput(COLLECTION_INPUT);
        verify(propertyWriter).setInput(DATA_INPUT);
        verify(propertyWriter).setCollectionOutput(COLLECTION_OUTPUT);
        verify(propertyWriter).setOutput(DATA_OUTPUT);
        verify(propertyWriter).setCompletionCondition(COMPLETION_CONDITION);
    }

    @Test
    public void testToFlowElementNonMI() {
        ((ReusableSubprocessTaskExecutionSet) node.getContent().getDefinition().getExecutionSet()).getIsMultipleInstance().setValue(false);
        assertEquals(propertyWriter, converter.toFlowElement(node));
        verifyCommonValues();
        verify(propertyWriter, never()).setIsSequential(anyBoolean());
        verify(propertyWriter, never()).setCollectionInput(anyString());
        verify(propertyWriter, never()).setInput(anyString());
        verify(propertyWriter, never()).setCollectionOutput(anyString());
        verify(propertyWriter, never()).setOutput(anyString());
        verify(propertyWriter, never()).setCompletionCondition(anyString());
    }

    private void verifyCommonValues() {
        verify(propertyWriterFactory).of(activityCaptor.capture());
        assertEquals(UUID, activityCaptor.getValue().getId());
        verify(propertyWriter).setName(NAME);
        verify(propertyWriter).setDocumentation(DOCUMENTATION);
        verify(propertyWriter).setOnEntryAction(ON_ENTRY_ACTION);
        verify(propertyWriter).setOnExitAction(ON_EXIT_ACTION);
        verify(propertyWriter).setCalledElement(CALLED_ELEMENT);
        verify(propertyWriter).setAsync(IS_ASYNC);
        verify(propertyWriter).setIndependent(INDEPENDENT);
        verify(propertyWriter).setWaitForCompletion(WAIT_FOR_COMPLETION);
        verify(propertyWriter).setAssignmentsInfo(ASSIGNMENTS_INFO);
        verify(propertyWriter).setSimulationSet(SIMULATION_SET);
        verify(propertyWriter).setAbsoluteBounds(node);
    }

    private ReusableSubprocessConverter tested =
            new ReusableSubprocessConverter(new PropertyWriterFactory());

    @Test
    public void testToFlowElement_case() {
        final BaseReusableSubprocess definition = new ReusableSubprocess();
        definition.getExecutionSet().setIsCase(new IsCase(true));
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(java.util.UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertTrue(CustomElement.isCase.of(propertyWriter.getFlowElement()).get());
    }

    @Test
    public void testToFlowElement_process() {
        final BaseReusableSubprocess definition = new ReusableSubprocess();
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(java.util.UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertFalse(CustomElement.isCase.of(propertyWriter.getFlowElement()).get());
    }

    @Test
    public void testToFlowElement_autostart() {
        final ReusableSubprocess definition = new ReusableSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(true));
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(java.util.UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertTrue(CustomElement.autoStart.of(propertyWriter.getFlowElement()).get());
    }

    @Test
    public void testToFlowElement_notautostart() {
        final ReusableSubprocess definition = new ReusableSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(false));
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(java.util.UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertFalse(CustomElement.autoStart.of(propertyWriter.getFlowElement()).get());
    }
}