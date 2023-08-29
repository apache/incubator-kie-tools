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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events;

import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    EList<LinkEventDefinition> sourceDefinitions;

    private static final String EVENT_ID = "EVENT_ID";
    private static final String LINK_NAME = "LINK_NAME";

    private final IntermediateLinkEventPostConverter converter = spy(new IntermediateLinkEventPostConverter());

    private EList<FlowElement> elementsInTopLevel;

    @Before
    public void init() {
        when(nodeWriter.getElement()).thenReturn(throwLinkEvent);
        when(throwLinkEvent.getId()).thenReturn(EVENT_ID);
        when(processWriter.getProcess()).thenReturn(process);

        when(throwDefinition.getName()).thenReturn(LINK_NAME);
        EList<EventDefinition> throwDefinitions = ECollections.singletonEList(throwDefinition);
        when(throwLinkEvent.getEventDefinitions()).thenReturn(throwDefinitions);

        when(catchDefinition.getName()).thenReturn(LINK_NAME);
        EList<EventDefinition> catchDefinitions = ECollections.singletonEList(catchDefinition);
        when(catchLinkEvent.getEventDefinitions()).thenReturn(catchDefinitions);
        when(catchDefinition.getSource()).thenReturn(sourceDefinitions);

        EList<EventDefinition> signalDefinitions = ECollections.singletonEList(mock(EventDefinition.class));
        when(catchSignalEvent.getEventDefinitions()).thenReturn(signalDefinitions);

        elementsInTopLevel = ECollections.asEList(
                mock(StartEvent.class),
                mock(EndEvent.class),
                throwLinkEvent,
                catchSignalEvent,
                catchLinkEvent
        );
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
        when(throwLinkEvent.getEventDefinitions()).thenReturn(ECollections.emptyEList());

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
        when(process.getFlowElements()).thenReturn(ECollections.emptyEList());

        converter.process(processWriter, nodeWriter, node);
        verify(converter).findTarget(eq(process), eq(EVENT_ID), eq(LINK_NAME));
        verify(throwDefinition, never()).setTarget(any());
    }

    @Test
    public void testThrowEventWithoutCatchEvent() {
        elementsInTopLevel = ECollections.asEList(
                mock(StartEvent.class),
                mock(EndEvent.class),
                throwLinkEvent,
                catchSignalEvent
        );
        when(process.getFlowElements()).thenReturn(elementsInTopLevel);

        converter.process(processWriter, nodeWriter, node);
        verify(converter).findTarget(eq(process), eq(EVENT_ID), eq(LINK_NAME));
        verify(converter).getCatchLinkEventWithSameName(eq(process), eq(LINK_NAME));
        verify(throwDefinition, never()).setTarget(any());
    }

    @Test
    public void testThrowEventWithoutCatchEventDefinitions() {
        when(catchLinkEvent.getEventDefinitions()).thenReturn(ECollections.emptyEList());

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
        verify(catchDefinition).getSource();
        verify(sourceDefinitions).add(throwDefinition);
    }
}
