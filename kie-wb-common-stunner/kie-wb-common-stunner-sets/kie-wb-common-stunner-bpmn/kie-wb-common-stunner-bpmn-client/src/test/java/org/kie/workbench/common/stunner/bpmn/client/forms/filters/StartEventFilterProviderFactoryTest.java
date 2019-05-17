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
package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private Element element;

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
        when(edge.getContent()).thenReturn(child);
        when(edge.getSourceNode()).thenReturn(parentNode);
        when(parentNode.asNode()).thenReturn(parentNode);
        when(parentNode.getContent()).thenReturn(parentView);
    }

    @Test
    public void testStartMessageEventFilterProviderShowIsInterruptingField() {
        testStartEventFilterProviderShowIsInterruptingField(StartMessageEvent.class);
    }

    @Test
    public void testStartErrorEventFilterProviderShowIsInterruptingField() {
        testStartEventFilterProviderShowIsInterruptingField(StartErrorEvent.class);
    }

    @Test
    public void testStartSignalEventFilterProviderShowIsInterruptingField() {
        testStartEventFilterProviderShowIsInterruptingField(StartSignalEvent.class);
    }

    @Test
    public void testStartTimerEventFilterProviderShowIsInterruptingField() {
        testStartEventFilterProviderShowIsInterruptingField(StartTimerEvent.class);
    }

    @Test
    public void testStartMessageEventFilterProviderHideIsInterruptingField() {
        testStartEventFilterProviderHideIsInterruptingField(StartMessageEvent.class);
    }

    @Test
    public void testStartErrorEventFilterProviderHideIsInterruptingField() {
        testStartEventFilterProviderHideIsInterruptingField(StartErrorEvent.class);
    }

    @Test
    public void testStartSignalEventFilterProviderHideIsInterruptingField() {

        testStartEventFilterProviderHideIsInterruptingField(StartSignalEvent.class);
    }

    @Test
    public void testStartTimerEventFilterProviderHideIsInterruptingField() {
        testStartEventFilterProviderHideIsInterruptingField(StartTimerEvent.class);
    }

    @Test
    public void testStartEventFilterProviderParentNull() {
        when(parentNode.asNode()).thenReturn(null);
        when(parentNode.getContent()).thenReturn(null);

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

    private void testStartEventFilterProviderShowIsInterruptingField(Class<?> filterClass) {
        when(parentView.getDefinition()).thenReturn(eventSubprocess);

        startEventFilterProvider = new StartEventFilterProvider(sessionManager, filterClass);
        assertEquals(filterClass,
                     startEventFilterProvider.getDefinitionType());

        Collection<FormElementFilter> formElementFilters = startEventFilterProvider.provideFilters(ELEMENT_UUID, definition);

        FormElementFilter formElementFilter = formElementFilters.iterator().next();

        assertEquals(1,
                     formElementFilters.size());
        assertEquals(ELEMENT_NAME,
                     formElementFilter.getElementName());

        assertTrue(formElementFilter.getPredicate().test(definition));
    }

    private void testStartEventFilterProviderHideIsInterruptingField(Class<?> filterClass) {
        when(parentView.getDefinition()).thenReturn(otherNode);

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
}
