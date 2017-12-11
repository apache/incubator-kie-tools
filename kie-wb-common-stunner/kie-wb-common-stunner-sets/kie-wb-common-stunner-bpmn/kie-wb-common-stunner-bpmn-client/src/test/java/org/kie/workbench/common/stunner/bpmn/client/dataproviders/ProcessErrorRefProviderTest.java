/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessErrorRefProviderTest {

    private static final int END_ERROR_EVENT_COUNT = 10;

    private static final String END_ERROR_EVENT_PREFIX = "END_ERROR_EVENT";

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private FormRenderingContext renderingContext;

    private ProcessErrorRefProvider provider;

    @Before
    public void setUp() throws Exception {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        provider = new ProcessErrorRefProvider(sessionManager);
    }

    @Test
    public void testGetSelectorDataWithNoValues() {
        @SuppressWarnings("unchecked")
        Iterable<Element> nodes = mock(Iterable.class);
        when(graph.nodes()).thenReturn(nodes);
        when(nodes.spliterator()).thenReturn(Spliterators.emptySpliterator());
        SelectorData selectorData = provider.getSelectorData(renderingContext);
        Map values = selectorData.getValues();
        assertTrue(values.isEmpty());
    }

    @Test
    public void testGetSelectorDataWithValues() {
        List<Element> nodes = mockEndErrorEventElements(END_ERROR_EVENT_COUNT);
        when(graph.nodes()).thenReturn(nodes);
        SelectorData selectorData = provider.getSelectorData(renderingContext);
        Map values = selectorData.getValues();
        assertEquals(END_ERROR_EVENT_COUNT,
                     values.size());
        for (int i = 0; i < END_ERROR_EVENT_COUNT; i++) {
            String expectedValue = END_ERROR_EVENT_PREFIX + i;
            assertTrue(values.containsKey(expectedValue));
            assertEquals(values.get(expectedValue),
                         expectedValue);
        }
    }

    private List<Element> mockEndErrorEventElements(int count) {
        List<Element> events = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            events.add(mockEndErrorEvent(END_ERROR_EVENT_PREFIX + i));
        }
        return events;
    }

    private Node mockEndErrorEvent(String errorRefValue) {
        EndErrorEvent event = new EndErrorEvent();
        event.setExecutionSet(new ErrorEventExecutionSet(new ErrorRef(errorRefValue)));
        Node node = mock(Node.class);
        View view = mock(View.class);
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(event);
        return node;
    }
}
