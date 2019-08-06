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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.function.Function;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.EventSubprocess;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.impl.UserTaskImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MatchTest {

    @Mock
    private BpmnNode defaultValue;

    @Mock
    private Function<UserTaskImpl, BpmnNode> assertUserTask;

    @Mock
    private Function<FlowElement, BpmnNode> fallback;

    @Mock
    private Function<SubProcess, BpmnNode> assertSubProcess;

    @Mock
    private BpmnNode fallbackNode;

    @Before
    public void setUp() {
        when(fallback.apply(any())).thenReturn(fallbackNode);
    }

    @Test
    public void whenExactlyTest() {
        UserTaskImpl element = (UserTaskImpl) Bpmn2Factory.eINSTANCE.createUserTask();
        Result<BpmnNode> result = match().apply(element);
        verify(assertUserTask).apply(element);
        assertTrue(result.isSuccess());
    }

    @Test
    public void whenTest() {
        EventSubprocess element = Bpmn2Factory.eINSTANCE.createEventSubprocess();
        Result<BpmnNode> result = match().apply(element);
        verify(assertSubProcess).apply(element);
        assertNotEquals(result.value(), defaultValue);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testFallback() {
        StartEvent element = Bpmn2Factory.eINSTANCE.createStartEvent();
        Result<BpmnNode> result = match()
                .orElse(fallback)
                .apply(element);
        verify(fallback).apply(element);
        assertNotEquals(result.value(), defaultValue);
        assertEquals(result.value(), fallbackNode);
        assertTrue(result.isSuccess());
    }

    @Test
    public void ignoreTestAuto() {
        ManualTask element = Bpmn2Factory.eINSTANCE.createManualTask();
        Result<BpmnNode> result = match().apply(element);
        assertEquals(result.value(), defaultValue);
        assertTrue(result.isIgnored());
        assertFalse(result.isFailure());
    }

    @Test
    public void ignoreTestIgnore() {
        ManualTask element = Bpmn2Factory.eINSTANCE.createManualTask();
        Result<BpmnNode> result = match()
                .mode(MarshallingRequest.Mode.IGNORE)
                .apply(element);
        assertEquals(result.value(), defaultValue);
        assertTrue(result.isIgnored());
        assertFalse(result.isFailure());
    }

    @Test
    public void ignoreTestError() {
        ManualTask element = Bpmn2Factory.eINSTANCE.createManualTask();
        Result<BpmnNode> result = match()
                .mode(MarshallingRequest.Mode.ERROR)
                .apply(element);
        assertEquals(result.value(), defaultValue);
        assertFalse(result.isIgnored());
        assertTrue(result.isFailure());
    }

    @Test
    public void missingTest() {
        ReceiveTask element = Bpmn2Factory.eINSTANCE.createReceiveTask();
        Result<BpmnNode> result = match().apply(element);
        assertEquals(result.value(), defaultValue);
        assertTrue(result.isFailure());
    }

    private Match<FlowElement, BpmnNode> match() {
        return Match.of(FlowElement.class, BpmnNode.class)
                .whenExactly(UserTaskImpl.class, assertUserTask)
                .when(SubProcess.class, assertSubProcess)
                .ignore(ManualTask.class)
                .missing(ReceiveTask.class)
                .defaultValue(defaultValue)
                .mode(MarshallingRequest.Mode.AUTO);
    }
}