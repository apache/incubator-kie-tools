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


package org.kie.workbench.common.stunner.forms.client.widgets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormsFlushManagerTest {

    private static final String GRAPH_UUID = "GraphUUID";
    private static final String ELEMENT_UUID = "ElementUUID";

    @Mock
    private FormsContainer formsContainer;

    @Mock
    private ClientSession clientSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    private FormsFlushManager tested;

    @Before
    public void setUp() throws Exception {
        tested = new FormsFlushManager();
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getUUID()).thenReturn(GRAPH_UUID);
    }

    @Test
    public void setCurrentContainer() {
        tested.container = formsContainer;
        tested.setCurrentContainer(formsContainer);
        assertEquals(formsContainer, tested.container);
    }

    @Test
    public void testOnFormsOpenedEvent() {
        tested.onFormsOpenedEvent(new FormPropertiesOpened(clientSession, ELEMENT_UUID, ""));
        assertEquals(ELEMENT_UUID, tested.formElementUUID);
    }

    @Test
    public void flush() {
        tested.onFormsOpenedEvent(new FormPropertiesOpened(clientSession, ELEMENT_UUID, ""));
        tested.container = null;
        tested.flush(clientSession);
        tested.container = formsContainer;
        tested.flush(clientSession);
        verify(formsContainer, times(1)).flush(GRAPH_UUID, ELEMENT_UUID);
    }

    @Test
    public void destroy() {
        FormsContainer formsContainer = mock(FormsContainer.class);
        tested.container = formsContainer;
        tested.destroy();
        assertEquals(null, tested.container);
    }
}