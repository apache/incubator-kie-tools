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

package org.kie.workbench.common.dmn.client.graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNGraphUtilsTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    @Mock
    private ClientSession clientSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    private DMNGraphUtils utils;

    @Before
    public void setup() {

        utils = new DMNGraphUtils(sessionManager, dmnDiagramUtils);

        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
    }

    @Test
    public void testGetDefinitions() {

        final Definitions expectedDefinitions = mock(Definitions.class);

        when(dmnDiagramUtils.getDefinitions(diagram)).thenReturn(expectedDefinitions);

        final Definitions actualDefinitions = utils.getDefinitions();

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
}
