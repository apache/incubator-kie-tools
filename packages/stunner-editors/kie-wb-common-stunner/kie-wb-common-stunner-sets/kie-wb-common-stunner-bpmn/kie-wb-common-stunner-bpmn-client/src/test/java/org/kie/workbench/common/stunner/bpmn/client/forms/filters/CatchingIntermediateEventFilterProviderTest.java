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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CatchingIntermediateEventFilterProviderTest {

    private static final String UUID = "UUID";
    private static final String ELEMENT_NAME = "executionSet.cancelActivity";
    private static final int EDGES_COUNT = 10;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession clientSession;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    Index graphIndex;

    @Mock
    private Element element;

    @Mock
    private Definition definition;

    @Mock
    private Node dockedNode;

    @Mock
    private Node noDockedNode;

    @Mock
    private Edge dockedEdge;

    @Mock
    private Dock dock;

    @Mock
    private View view;

    @Before
    public void setUp() {
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);

        when(dockedEdge.getContent()).thenReturn(dock);

        List<Edge> edgesWithDock = mockEdges(EDGES_COUNT);
        edgesWithDock.add(dockedEdge);
        when(dockedNode.getInEdges()).thenReturn(edgesWithDock);
        when(dockedNode.asNode()).thenReturn(dockedNode);
        when(dockedNode.getContent()).thenReturn(view);

        List<Edge> edgesNoWithDock = mockEdges(EDGES_COUNT);
        when(noDockedNode.getInEdges()).thenReturn(edgesNoWithDock);
        when(noDockedNode.getContent()).thenReturn(view);
    }

    @Test
    public void testFilterProviderShowCancelActivityField() {
        BaseCatchingIntermediateEvent[] testedClasses = {
                new IntermediateTimerEvent(),
                new IntermediateConditionalEvent(),
                new IntermediateSignalEventCatching(),
                new IntermediateTimerEvent(),
                new IntermediateMessageEventCatching()
        };
        when(graphIndex.getNode(UUID)).thenReturn(dockedNode);
        Stream.of(testedClasses).forEach(catchingIntermediateEvent -> testFilterProviderShowCancelActivityField(catchingIntermediateEvent));
    }

    @Test
    public void testFilterProviderDontShowCancelActivityField() {
        BaseCatchingIntermediateEvent[] test1Classes = {
                new IntermediateTimerEvent(),
                new IntermediateErrorEventCatching(),
                new IntermediateConditionalEvent(),
                new IntermediateCompensationEvent(),
                new IntermediateSignalEventCatching(),
                new IntermediateTimerEvent(),
                new IntermediateMessageEventCatching()
        };
        when(graphIndex.getNode(UUID)).thenReturn(noDockedNode);
        Stream.of(test1Classes).forEach(catchingIntermediateEvent -> testFilterProviderDontShowCancelActivityField(catchingIntermediateEvent));

        BaseCatchingIntermediateEvent[] test2Classes = {
                new IntermediateErrorEventCatching(),
                new IntermediateCompensationEvent(),
        };
        when(graphIndex.getNode(UUID)).thenReturn(dockedNode);
        Stream.of(test2Classes).forEach(clazz -> testFilterProviderDontShowCancelActivityField(clazz));
    }

    private void testFilterProviderShowCancelActivityField(BaseCatchingIntermediateEvent filterEvent) {
        when(view.getDefinition()).thenReturn(filterEvent);

        Class<?> filterClass = filterEvent.getClass();
        CatchingIntermediateEventFilterProvider filterProvider =
                new CatchingIntermediateEventFilterProvider(sessionManager, filterClass);
        assertEquals(filterClass, filterProvider.getDefinitionType());

        Collection<FormElementFilter> formElementFilters = filterProvider.provideFilters(UUID, definition);
        assertEquals(1, formElementFilters.size());

        FormElementFilter formElementFilter = formElementFilters.iterator().next();
        assertEquals(formElementFilter.getElementName(), ELEMENT_NAME);

        assertTrue(formElementFilter.getPredicate().test(mock(Object.class)));
    }

    private void testFilterProviderDontShowCancelActivityField(BaseCatchingIntermediateEvent filterEvent) {
        when(view.getDefinition()).thenReturn(filterEvent);

        Class<?> filterClass = filterEvent.getClass();
        CatchingIntermediateEventFilterProvider filterProvider =
                new CatchingIntermediateEventFilterProvider(sessionManager,filterClass);
        assertEquals(filterClass, filterProvider.getDefinitionType());

        Collection<FormElementFilter> formElementFilters = filterProvider.provideFilters(UUID, definition);
        assertEquals(1, formElementFilters.size());

        FormElementFilter formElementFilter = formElementFilters.iterator().next();
        assertEquals(formElementFilter.getElementName(), ELEMENT_NAME);

        assertFalse(formElementFilter.getPredicate().test(mock(Object.class)));
    }

    private List<Edge> mockEdges(int count) {
        List<Edge> edges = new ArrayList<>();
        Edge edge;
        Object content;
        for (int i = 0; i < count; i++) {
            edge = mock(Edge.class);
            content = mock(Object.class);
            when(edge.getContent()).thenReturn(content);
        }
        return edges;
    }
}
