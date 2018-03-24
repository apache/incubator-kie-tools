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
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
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
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jgroups.util.Util.assertEquals;
import static org.jgroups.util.Util.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StunnerFilterProviderManagerImplTest {

    @Mock
    protected Node parentNode;
    private String ELEMENT_UUID = "UUID";
    @Mock
    private ClientSession session;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private GraphUtils graphUtils;

    @Mock
    private Element element;

    @Mock
    private Definition definition;

    @Mock
    private View parentView;

    @Mock
    private EventSubprocess eventSubprocess;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Node node;

    @Mock
    private Index graphIndex;

    @Mock
    private Edge edge;

    @Mock
    private Child child;

    private List<Edge> inEdges;

    private StunnerFilterProviderManagerImpl stunnerFilterProviderManager;

    @Before
    public void setUp() throws Exception {
        eventSubprocess = new EventSubprocess();
        inEdges = Arrays.asList(edge);
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
    public void getStartErrorEventFilterForDefinitionTest() {
        when(parentView.getDefinition()).thenReturn(eventSubprocess);

        StartErrorEvent startErrorEvent = new StartErrorEvent();

        stunnerFilterProviderManager = new StunnerFilterProviderManagerImpl(sessionManager, graphUtils);
        stunnerFilterProviderManager.init();
        Collection<FormElementFilter> filters = stunnerFilterProviderManager.getFilterForDefinition(ELEMENT_UUID, element, startErrorEvent);
        FormElementFilter filter = filters.iterator().next();

        assertEquals(1, filters.size());
        assertEquals("executionSet.isInterrupting", filter.getElementName());
        assertTrue(filter.getPredicate().test(startErrorEvent));
    }

    @Test
    public void getStartMessageEventFilterForDefinitionTest() {
        when(parentView.getDefinition()).thenReturn(eventSubprocess);

        StartMessageEvent startMessageEvent = new StartMessageEvent();

        stunnerFilterProviderManager = new StunnerFilterProviderManagerImpl(sessionManager, graphUtils);
        stunnerFilterProviderManager.init();
        Collection<FormElementFilter> filters = stunnerFilterProviderManager.getFilterForDefinition(ELEMENT_UUID, element, startMessageEvent);
        FormElementFilter filter = filters.iterator().next();

        assertEquals(1, filters.size());
        assertEquals("executionSet.isInterrupting", filter.getElementName());
        assertTrue(filter.getPredicate().test(startMessageEvent));
    }

    @Test
    public void getStartTimerEventFilterForDefinitionTest() {
        when(parentView.getDefinition()).thenReturn(eventSubprocess);

        StartTimerEvent startTimerEvent = new StartTimerEvent();

        stunnerFilterProviderManager = new StunnerFilterProviderManagerImpl(sessionManager, graphUtils);
        stunnerFilterProviderManager.init();
        Collection<FormElementFilter> filters = stunnerFilterProviderManager.getFilterForDefinition(ELEMENT_UUID, element, startTimerEvent);
        FormElementFilter filter = filters.iterator().next();

        assertEquals(1, filters.size());
        assertEquals("executionSet.isInterrupting", filter.getElementName());
        assertTrue(filter.getPredicate().test(startTimerEvent));
    }

    @Test
    public void getStartSignalEventFilterForDefinitionTest() {
        when(parentView.getDefinition()).thenReturn(eventSubprocess);

        StartSignalEvent startSignalEvent = new StartSignalEvent();

        stunnerFilterProviderManager = new StunnerFilterProviderManagerImpl(sessionManager, graphUtils);
        stunnerFilterProviderManager.init();
        Collection<FormElementFilter> filters = stunnerFilterProviderManager.getFilterForDefinition(ELEMENT_UUID, element, startSignalEvent);
        FormElementFilter filter = filters.iterator().next();

        assertEquals(1, filters.size());
        assertEquals("executionSet.isInterrupting", filter.getElementName());
        assertTrue(filter.getPredicate().test(startSignalEvent));
    }

    @Test
    public void getFilterForDefinitionEmptyTest() {

        stunnerFilterProviderManager = new StunnerFilterProviderManagerImpl(sessionManager, graphUtils);
        stunnerFilterProviderManager.init();
        Collection<FormElementFilter> filter = stunnerFilterProviderManager.getFilterForDefinition(ELEMENT_UUID, element, definition);
        assertEquals(Collections.emptyList(), filter);
    }
}
