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

package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StartEventFilterProviderFactoryTest {

    private static String ELEMENT_UUID = "UUID";
    private static String ELEMENT_NAME = "executionSet.isInterrupting";

    @Mock
    protected Node parentNode;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private Definition definition;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Index graphIndex;

    @Mock
    private Node node;

    @Mock
    private Edge edge;

    @Mock
    private Child child;

    @Mock
    private View view;

    @Mock
    private View parentView;

    private List<Edge> inEdges;

    private EventSubprocess eventSubprocess;
    private MultipleInstanceSubprocess otherNode;
    private StartEventFilterProvider startEventFilterProvider;

    @Before
    public void setUp() throws Exception {
        inEdges = Arrays.asList(edge);
        eventSubprocess = new EventSubprocess();
        otherNode = new MultipleInstanceSubprocess();

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(canvasHandler.getGraphIndex().getNode(ELEMENT_UUID)).thenReturn(node);
        when(node.getInEdges()).thenReturn(inEdges);
        when(node.getContent()).thenReturn(view);
        when(edge.getContent()).thenReturn(child);
        when(edge.getSourceNode()).thenReturn(parentNode);
        when(parentNode.asNode()).thenReturn(parentNode);
        when(parentNode.getContent()).thenReturn(parentView);
    }

    @Test
    public void testFilterProviderShowIsInterruptingField() {
        BaseStartEvent[] testedClasses = {
                new StartSignalEvent(),
                new StartTimerEvent(),
                new StartConditionalEvent(),
                new StartEscalationEvent(),
                new StartMessageEvent()
        };

        when(parentView.getDefinition()).thenReturn(eventSubprocess);
        Stream.of(testedClasses).forEach(startEvent -> testStartEventFilterProviderShowIsInterruptingField(startEvent));
    }

    @Test
    public void testFilterProviderHideIsInterruptingField() {
        BaseStartEvent[] test1Classes = {
                new StartNoneEvent(),
                new StartCompensationEvent(),
                new StartSignalEvent(),
                new StartTimerEvent(),
                new StartConditionalEvent(),
                new StartErrorEvent(),
                new StartEscalationEvent(),
                new StartMessageEvent()
        };
        when(parentView.getDefinition()).thenReturn(otherNode);
        Stream.of(test1Classes).
                forEach(catchingIntermediateEvent ->
                                testStartEventFilterProviderHideIsInterruptingField(catchingIntermediateEvent));

        BaseStartEvent[] test2Classes = {
                new StartNoneEvent(),
                new StartCompensationEvent(),
                new StartErrorEvent(),
        };
        when(parentView.getDefinition()).thenReturn(eventSubprocess);
        Stream.of(test2Classes).forEach(clazz -> testStartEventFilterProviderHideIsInterruptingField(clazz));
    }

    @Test
    public void testStartEventFilterProviderParentNull() {
        when(parentNode.asNode()).thenReturn(null);

        Class<?> filterClass = StartErrorEvent.class;

        startEventFilterProvider = new StartEventFilterProvider(sessionManager, filterClass);
        assertEquals(filterClass,
                     startEventFilterProvider.getDefinitionType());

        Collection<FormElementFilter> formElementFilters = startEventFilterProvider.provideFilters(ELEMENT_UUID, definition);

        FormElementFilter formElementFilter = formElementFilters.iterator().next();

        assertEquals(1,
                     formElementFilters.size());
        assertEquals(ELEMENT_NAME,
                     formElementFilter.getElementName());

        assertFalse(formElementFilter.getPredicate().test(definition));
    }

    private void testStartEventFilterProviderShowIsInterruptingField(BaseStartEvent filterEvent) {
        when(view.getDefinition()).thenReturn(filterEvent);

        Class<?> filterClass = filterEvent.getClass();
        startEventFilterProvider = new StartEventFilterProvider(sessionManager, filterClass);
        assertEquals(filterClass, startEventFilterProvider.getDefinitionType());

        Collection<FormElementFilter> formElementFilters = startEventFilterProvider.provideFilters(ELEMENT_UUID, definition);

        FormElementFilter formElementFilter = formElementFilters.iterator().next();

        assertEquals(1, formElementFilters.size());
        assertEquals(ELEMENT_NAME, formElementFilter.getElementName());

        assertTrue(formElementFilter.getPredicate().test(definition));
    }

    private void testStartEventFilterProviderHideIsInterruptingField(BaseStartEvent filterEvent) {
        when(parentView.getDefinition()).thenReturn(otherNode);
        when(view.getDefinition()).thenReturn(filterEvent);

        Class<?> filterClass = filterEvent.getClass();
        startEventFilterProvider = new StartEventFilterProvider(sessionManager, filterClass);
        assertEquals(filterClass, startEventFilterProvider.getDefinitionType());

        Collection<FormElementFilter> formElementFilters = startEventFilterProvider.provideFilters(ELEMENT_UUID, definition);

        FormElementFilter formElementFilter = formElementFilters.iterator().next();

        assertEquals(1, formElementFilters.size());
        assertEquals(ELEMENT_NAME, formElementFilter.getElementName());

        assertFalse(formElementFilter.getPredicate().test(definition));
    }
}
