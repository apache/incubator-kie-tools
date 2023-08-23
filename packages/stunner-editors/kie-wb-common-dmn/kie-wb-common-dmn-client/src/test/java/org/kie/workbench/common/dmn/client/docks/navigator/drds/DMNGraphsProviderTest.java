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

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNGraphsProviderTest {

    private DMNGraphsProvider provider;

    @Mock
    private DMNDiagramsSession diagramsSession;

    @Before
    public void setup() {
        provider = new DMNGraphsProvider(diagramsSession);
    }

    @Test
    public void testIsGlobalGraphSelected() {

        when(diagramsSession.isGlobalGraphSelected()).thenReturn(true);

        final boolean isGlobalGraphSelected = provider.isGlobalGraphSelected();

        verify(diagramsSession).isGlobalGraphSelected();
        assertTrue(isGlobalGraphSelected);
    }

    @Test
    public void testGetGraphs() {

        final List<Graph> graphs = mock(List.class);

        when(diagramsSession.getGraphs()).thenReturn(graphs);

        final List<Graph> actual = provider.getGraphs();

        assertEquals(graphs, actual);
    }

    @Test
    public void testGetNonGlobalGraphs() {

        final List<Graph> graphs = mock(List.class);

        when(diagramsSession.getNonGlobalGraphs()).thenReturn(graphs);

        final List<Graph> actual = provider.getNonGlobalGraphs();

        assertEquals(graphs, actual);
    }

    @Test
    public void testGetDiagram() {

        final String diagramId = "diagram Id";
        final Diagram diagram = mock(Diagram.class);
        when(diagramsSession.getDiagram(diagramId)).thenReturn(diagram);

        final Diagram actualDiagram = provider.getDiagram(diagramId);

        assertEquals(diagram, actualDiagram);
    }

    @Test
    public void testGetCurrentDiagramId() {

        final String currentDiagramId = "currentDiagramId";
        when(diagramsSession.getCurrentDiagramId()).thenReturn(currentDiagramId);

        final String actual = provider.getCurrentDiagramId();
        assertEquals(currentDiagramId, actual);
    }
}
