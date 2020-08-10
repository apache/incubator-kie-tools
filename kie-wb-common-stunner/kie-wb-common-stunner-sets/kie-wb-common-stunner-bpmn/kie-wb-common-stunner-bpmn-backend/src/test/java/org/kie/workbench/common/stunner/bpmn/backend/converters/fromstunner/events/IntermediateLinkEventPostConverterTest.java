/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.ThrowEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntermediateLinkEventPostConverterTest {

    @Mock
    private ProcessPropertyWriter processWriter;

    @Mock
    private BasePropertyWriter nodeWriter;

    @Mock
    private Node<View<? extends BPMNViewDefinition>, ?> node;

    @Mock
    private ThrowEvent throwLinkEvent;

    @Mock
    private CatchEvent catchLinkEvent;

    @Mock
    private CatchEvent catchSignalEvent;

    @Mock
    private LinkEventDefinition throwDefinition;

    @Mock
    private LinkEventDefinition catchDefinition;

    @Mock
    private Process process;

    private static final String EVENT_ID = "EVENT_ID";
    private static final String LINK_NAME = "LINK_NAME";

    private IntermediateLinkEventPostConverter converter = spy(new IntermediateLinkEventPostConverter());

    private final List<EventDefinition> throwDefinitions = new ArrayList<>();
    private final List<EventDefinition> catchDefinitions = new ArrayList<>();
    private final List<FlowElement> elementsInTopLevel = new ArrayList<>();

    @Before
    public void init() {
        when(nodeWriter.getElement()).thenReturn(throwLinkEvent);
        when(throwLinkEvent.getId()).thenReturn(EVENT_ID);
        when(processWriter.getProcess()).thenReturn(process);

        when(throwDefinition.getName()).thenReturn(LINK_NAME);
        throwDefinitions.add(throwDefinition);
        when(throwLinkEvent.getEventDefinitions()).thenReturn(throwDefinitions);

        when(catchDefinition.getName()).thenReturn(LINK_NAME);
        catchDefinitions.add(catchDefinition);
        when(catchLinkEvent.getEventDefinitions()).thenReturn(catchDefinitions);

        List<EventDefinition> signalDefinitions = new ArrayList<>();
        signalDefinitions.add(mock(EventDefinition.class));
        when(catchSignalEvent.getEventDefinitions()).thenReturn(signalDefinitions);

        elementsInTopLevel.add(mock(StartEvent.class));
        elementsInTopLevel.add(mock(EndEvent.class));
        elementsInTopLevel.add(throwLinkEvent);
        elementsInTopLevel.add(catchSignalEvent);
        elementsInTopLevel.add(catchLinkEvent);
        when(process.getFlowElements()).thenReturn(elementsInTopLevel);
    }

    @Test
    public void testNullEventDefinitions() {
        when(throwLinkEvent.getEventDefinitions()).thenReturn(null);

        converter.process(processWriter, nodeWriter, node);
        verify(converter, never()).addTargetRef(any(), any(), any());
    }

    @Test
    public void testEmptyEventDefinitions() {
        when(throwLinkEvent.getEventDefinitions()).thenReturn(Collections.emptyList());

        converter.process(processWriter, nodeWriter, node);
        verify(converter, never()).addTargetRef(any(), any(), any());
    }

    @Test
    public void testEmptyLinkName() {
        when(throwDefinition.getName()).thenReturn(null);

        converter.process(processWriter, nodeWriter, node);
        verify(converter, never()).findTarget(any(), any(), any());
    }

    @Test
    public void testNoTargetsForEmptyGraph() {
        when(process.getFlowElements()).thenReturn(Collections.emptyList());

        converter.process(processWriter, nodeWriter, node);
        verify(converter).findTarget(eq(process), eq(EVENT_ID), eq(LINK_NAME));
        verify(throwDefinition, never()).setTarget(any());
    }

    @Test
    public void testThrowEventWithoutCatchEvent() {
        elementsInTopLevel.remove(catchLinkEvent);

        converter.process(processWriter, nodeWriter, node);
        verify(converter).findTarget(eq(process), eq(EVENT_ID), eq(LINK_NAME));
        verify(converter).getCatchLinkEventWithSameName(eq(process), eq(LINK_NAME));
        verify(throwDefinition, never()).setTarget(any());
    }

    @Test
    public void testThrowEventWithoutCatchEventDefinitions() {
        when(catchLinkEvent.getEventDefinitions()).thenReturn(Collections.emptyList());

        converter.process(processWriter, nodeWriter, node);
        verify(converter).findTarget(eq(process), eq(EVENT_ID), eq(LINK_NAME));
        verify(converter).getCatchLinkEventWithSameName(eq(process), eq(LINK_NAME));
        verify(throwDefinition, never()).setTarget(any());
    }

    @Test
    public void testThrowEventCatchEventOnTheTopLevel() {
        converter.process(processWriter, nodeWriter, node);
        verify(converter).findTarget(eq(process), eq(EVENT_ID), eq(LINK_NAME));
        verify(converter).getCatchLinkEventWithSameName(eq(process), eq(LINK_NAME));
        verify(throwDefinition).setTarget(catchDefinition);
    }
}
