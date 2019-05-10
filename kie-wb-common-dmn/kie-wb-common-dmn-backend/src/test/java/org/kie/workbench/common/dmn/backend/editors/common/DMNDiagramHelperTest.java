/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.editors.common;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDiagramHelperTest {

    @Mock
    private DiagramService diagramService;

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    @Mock
    private Diagram<Graph, Metadata> diagram;

    @Mock
    private Path path;

    private DMNDiagramHelper helper;

    @Before
    public void setup() {
        helper = new DMNDiagramHelper(diagramService, dmnDiagramUtils);
    }

    @Test
    public void testGetNodes() {

        final DRGElement drgElement = mock(DRGElement.class);
        final List<DRGElement> expectedNodes = singletonList(drgElement);

        when(dmnDiagramUtils.getNodes(diagram)).thenReturn(expectedNodes);

        final List<DRGElement> actualNodes = helper.getNodes(diagram);

        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    public void testGetItemDefinitionsByDiagram() {

        final Definitions definitions = mock(Definitions.class);
        final List<ItemDefinition> expectedItemDefinitions = asList(mock(ItemDefinition.class), mock(ItemDefinition.class));

        when(definitions.getItemDefinition()).thenReturn(expectedItemDefinitions);
        when(dmnDiagramUtils.getDefinitions(diagram)).thenReturn(definitions);

        final List<ItemDefinition> actualItemDefinitions = helper.getItemDefinitions(diagram);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testGetNamespaceByDiagram() {

        final String expectedNamespace = "://namespace";

        when(dmnDiagramUtils.getNamespace(diagram)).thenReturn(expectedNamespace);

        final String actualNamespace = helper.getNamespace(diagram);

        assertEquals(expectedNamespace, actualNamespace);
    }

    @Test
    public void testGetDiagramByPath() {

        when(diagramService.getDiagramByPath(path)).thenReturn(diagram);

        Diagram<Graph, Metadata> actualDiagram = helper.getDiagramByPath(path);

        assertEquals(diagram, actualDiagram);
    }

    @Test
    public void testGetNamespaceByPath() {

        final String expectedNamespace = "://namespace";

        when(dmnDiagramUtils.getNamespace(diagram)).thenReturn(expectedNamespace);
        when(diagramService.getDiagramByPath(path)).thenReturn(diagram);

        final String actualNamespace = helper.getNamespace(path);

        assertEquals(expectedNamespace, actualNamespace);
    }
}
