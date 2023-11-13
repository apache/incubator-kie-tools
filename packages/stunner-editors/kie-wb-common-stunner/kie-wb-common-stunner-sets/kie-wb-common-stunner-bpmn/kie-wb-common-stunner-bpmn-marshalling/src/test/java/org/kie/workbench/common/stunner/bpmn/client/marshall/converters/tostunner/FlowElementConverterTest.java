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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.DataStoreReference;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.activities.CallActivityConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.artifacts.DataObjectConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.artifacts.TextAnnotationConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.EndEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.IntermediateCatchEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.IntermediateThrowEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.StartEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.gateways.GatewayConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes.SubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.tasks.TaskConverter;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FlowElementConverterTest {

    private FlowElementConverter tested;

    @Mock
    private BaseConverterFactory converterFactory;

    @Mock
    private DefinitionResolver definitionResolver;

    @Mock
    private StartEventConverter startEventConverter;

    @Mock
    private EndEventConverter endEventConverter;

    @Mock
    private IntermediateCatchEventConverter eventConverter;

    @Mock
    private IntermediateThrowEventConverter throwEventConverter;

    @Mock
    private TaskConverter taskConverter;

    @Mock
    private GatewayConverter gatewayConverter;

    @Mock
    private SubProcessConverter subProcessConverter;

    @Mock
    private CallActivityConverter callActivityConverter;

    @Mock
    private TextAnnotationConverter textAnnotationConverter;

    @Mock
    private DataObjectConverter dataObjectConverter;

    @Before
    public void setUp() {
        when(converterFactory.startEventConverter()).thenReturn(startEventConverter);
        when(converterFactory.endEventConverter()).thenReturn(endEventConverter);
        when(converterFactory.intermediateCatchEventConverter()).thenReturn(eventConverter);
        when(converterFactory.taskConverter()).thenReturn(taskConverter);
        when(converterFactory.intermediateThrowEventConverter()).thenReturn(throwEventConverter);
        when(converterFactory.gatewayConverter()).thenReturn(gatewayConverter);
        when(converterFactory.subProcessConverter()).thenReturn(subProcessConverter);
        when(converterFactory.callActivityConverter()).thenReturn(callActivityConverter);
        when(converterFactory.getDefinitionResolver()).thenReturn(definitionResolver);
        when(converterFactory.textAnnotationConverter()).thenReturn(textAnnotationConverter);
        when(converterFactory.dataObjectConverter()).thenReturn(dataObjectConverter);
        tested = new FlowElementConverter(converterFactory);
    }

    @Test
    public void convertSupported() {
        StartEvent startEvent = mock(StartEvent.class);
        tested.convertNode(startEvent);
        verify(startEventConverter).convert(startEvent);

        EndEvent endEvent = mock(EndEvent.class);
        tested.convertNode(endEvent);
        verify(endEventConverter).convert(endEvent);

        BoundaryEvent boundaryEvent = mock(BoundaryEvent.class);
        tested.convertNode(boundaryEvent);
        verify(eventConverter).convertBoundaryEvent(boundaryEvent);

        IntermediateCatchEvent intermediateCatchEvent = mock(IntermediateCatchEvent.class);
        tested.convertNode(intermediateCatchEvent);

        verify(eventConverter).convert(intermediateCatchEvent);

        IntermediateThrowEvent intermediateThrowEvent = mock(IntermediateThrowEvent.class);
        tested.convertNode(intermediateThrowEvent);
        verify(throwEventConverter).convert(intermediateThrowEvent);

        Task task = mock(Task.class);
        tested.convertNode(task);
        verify(taskConverter).convert(task);

        Gateway gateway = mock(Gateway.class);
        tested.convertNode(gateway);
        verify(gatewayConverter).convert(gateway);

        SubProcess subProcess = mock(SubProcess.class);
        tested.convertNode(subProcess);
        verify(subProcessConverter).convertSubProcess(subProcess);

        CallActivity callActivity = mock(CallActivity.class);
        tested.convertNode(callActivity);
        verify(callActivityConverter).convert(callActivity);

        TextAnnotation textAnnotation = mock(TextAnnotation.class);
        tested.convertNode(textAnnotation);
        verify(textAnnotationConverter).convert(textAnnotation);

        DataObjectReference dataObjectReference = mock(DataObjectReference.class);
        tested.convertNode(dataObjectReference);
        verify(dataObjectConverter).convert(dataObjectReference);
    }

    // TODO: Kogito - @Test
    public void convertUnsupported() {
        assertUnsupported(DataStoreReference.class);
        assertUnsupported(DataObject.class);
    }

    private <T extends FlowElement> void assertUnsupported(Class<T> type) {
        T element = mock(type);
        Result<BpmnNode> result = tested.convertNode(element);
        assertTrue(result.isIgnored());
        assertFalse(result.isSuccess());
        assertFalse(result.isFailure());
    }
}