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

package org.kie.workbench.common.dmn.client.graph;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DMNGraphUtilsTest {

    private static final String NAME = "name";

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    @Mock
    private ClientSession clientSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private Metadata metadata;

    private DMNGraphUtils utils;

    private GraphImpl<DefinitionSet> graph;

    private DiagramImpl diagram;

    @Before
    public void setup() {
        this.utils = new DMNGraphUtils(sessionManager, dmnDiagramUtils, dmnDiagramsSession);
        this.graph = new GraphImpl<>(UUID.uuid(), new GraphNodeStoreImpl());
        this.diagram = new DiagramImpl(NAME, graph, metadata);
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
    }

    @Test
    public void testGetDefinitions() {

        final Definitions expectedDefinitions = mock(Definitions.class);

        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(diagram);
        when(dmnDiagramUtils.getDefinitions(diagram)).thenReturn(expectedDefinitions);

        final Definitions actualDefinitions = utils.getModelDefinitions();

        assertNotNull(actualDefinitions);
        assertEquals(expectedDefinitions, actualDefinitions);
    }

    @Test
    public void testGetDefinitionsWithDiagram() {

        final Definitions expectedDefinitions = mock(Definitions.class);
        final Diagram diagram = mock(Diagram.class);

        when(dmnDiagramUtils.getDefinitions(diagram)).thenReturn(expectedDefinitions);

        final Definitions actualDefinitions = utils.getDefinitions(diagram);

        assertNotNull(actualDefinitions);
        assertEquals(expectedDefinitions, actualDefinitions);
    }

    @Test
    public void testGetDefinitionsWithNoNodes() {
        assertNull(utils.getModelDefinitions());
    }

    @Test
    public void testGetCanvasHandler() {
        final CanvasHandler actualCanvasHandler = utils.getCanvasHandler();
        assertEquals(canvasHandler, actualCanvasHandler);
    }

    @Test
    public void testGetModelDRGElements() {

        final List<DRGElement> expectedDRGElements = asList(mock(DRGElement.class), mock(DRGElement.class));
        when(dmnDiagramsSession.getModelDRGElements()).thenReturn(expectedDRGElements);

        final List<DRGElement> actualDRGElements = utils.getModelDRGElements();

        assertEquals(expectedDRGElements, actualDRGElements);
    }

    @Test
    public void testGetNodeStream() {

        final Stream<Node> expectedStream = Stream.of(mock(Node.class));

        when(dmnDiagramsSession.getCurrentGraphDiagram()).thenReturn(diagram);
        when(dmnDiagramUtils.getNodeStream(diagram)).thenReturn(expectedStream);

        final Stream<Node> actualStream = utils.getNodeStream();

        assertEquals(expectedStream, actualStream);
    }
}
