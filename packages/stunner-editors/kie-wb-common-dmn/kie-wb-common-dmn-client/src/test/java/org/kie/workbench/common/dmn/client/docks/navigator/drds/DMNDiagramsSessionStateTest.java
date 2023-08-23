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
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramsSessionStateTest {

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    @Mock
    private Diagram stunnerDiagram1;

    @Mock
    private Diagram stunnerDiagram2;

    @Mock
    private Diagram stunnerDiagram3;

    @Mock
    private DMNDiagramElement dmnDiagram1;

    @Mock
    private DMNDiagramElement dmnDiagram2;

    @Mock
    private DMNDiagramElement dmnDiagram3;

    private String id1 = "1111-1111-1111";

    private String id2 = "2222-2222-2222";

    private String id3 = "3333-3333-3333";

    private String name1 = "DRG";

    private String name2 = "Page 1";

    private String name3 = "Page 2";

    private DMNDiagramsSessionState sessionState;

    @Before
    public void setup() {
        sessionState = new DMNDiagramsSessionState(dmnDiagramUtils);

        when(dmnDiagram1.getId()).thenReturn(new Id(id1));
        when(dmnDiagram2.getId()).thenReturn(new Id(id2));
        when(dmnDiagram3.getId()).thenReturn(new Id(id3));
        when(dmnDiagram1.getName()).thenReturn(new Name(name1));
        when(dmnDiagram2.getName()).thenReturn(new Name(name2));
        when(dmnDiagram3.getName()).thenReturn(new Name(name3));

        sessionState.getDiagramsByDiagramId().put(id1, stunnerDiagram1);
        sessionState.getDiagramsByDiagramId().put(id2, stunnerDiagram2);
        sessionState.getDiagramsByDiagramId().put(id3, stunnerDiagram3);
        sessionState.getDMNDiagramsByDiagramId().put(id1, dmnDiagram1);
        sessionState.getDMNDiagramsByDiagramId().put(id2, dmnDiagram2);
        sessionState.getDMNDiagramsByDiagramId().put(id3, dmnDiagram3);
    }

    @Test
    public void testGetDiagram() {
        assertEquals(stunnerDiagram1, sessionState.getDiagram(id1));
        assertEquals(stunnerDiagram2, sessionState.getDiagram(id2));
        assertEquals(stunnerDiagram3, sessionState.getDiagram(id3));
    }

    @Test
    public void testGetDMNDiagramElement() {
        assertEquals(dmnDiagram1, sessionState.getDMNDiagramElement(id1));
        assertEquals(dmnDiagram2, sessionState.getDMNDiagramElement(id2));
        assertEquals(dmnDiagram3, sessionState.getDMNDiagramElement(id3));
    }

    @Test
    public void testGetDiagramTuple() {
        final DMNDiagramTuple tuple = sessionState.getDiagramTuple(id1);
        assertEquals(dmnDiagram1, tuple.getDMNDiagram());
        assertEquals(stunnerDiagram1, tuple.getStunnerDiagram());
    }

    @Test
    public void testGetDMNDiagrams() {
        final List<DMNDiagramTuple> tuples = sessionState.getDMNDiagrams();

        assertEquals(3, tuples.size());

        final List<DMNDiagramElement> dmnDiagrams = tuples.stream().map(DMNDiagramTuple::getDMNDiagram).collect(Collectors.toList());
        final List<Diagram> stunnerDiagrams = tuples.stream().map(DMNDiagramTuple::getStunnerDiagram).collect(Collectors.toList());

        assertTrue(dmnDiagrams.contains(dmnDiagram1));
        assertTrue(dmnDiagrams.contains(dmnDiagram2));
        assertTrue(dmnDiagrams.contains(dmnDiagram3));
        assertTrue(stunnerDiagrams.contains(stunnerDiagram1));
        assertTrue(stunnerDiagrams.contains(stunnerDiagram2));
        assertTrue(stunnerDiagrams.contains(stunnerDiagram3));
    }

    @Test
    public void testGetCurrentDMNDiagramElement() {
        sessionState.setCurrentDMNDiagramElement(dmnDiagram2);

        final Optional<DMNDiagramElement> actual = sessionState.getCurrentDMNDiagramElement();

        assertTrue(actual.isPresent());
        assertEquals(dmnDiagram2, actual.get());
    }

    @Test
    public void testGetCurrentDMNDiagramElementWhenCurrentDiagramIsNotPresent() {
        sessionState.setCurrentDMNDiagramElement(null);

        final Optional<DMNDiagramElement> actual = sessionState.getCurrentDMNDiagramElement();

        assertTrue(actual.isPresent());
        assertEquals(dmnDiagram1, actual.get()); // Returns the DRG
    }

    @Test
    public void testGetCurrentDiagram() {
        sessionState.setCurrentDMNDiagramElement(dmnDiagram1);

        final Optional<Diagram> actual = sessionState.getCurrentDiagram();

        assertTrue(actual.isPresent());
        assertEquals(stunnerDiagram1, actual.get());
    }

    @Test
    public void testGetDRGDiagram() {
        assertEquals(stunnerDiagram1, sessionState.getDRGDiagram());
    }

    @Test
    public void testGetDRGDiagramElement() {
        assertEquals(dmnDiagram1, sessionState.getDRGDiagramElement());
    }

    @Test
    public void testGetDRGDiagramTuple() {
        final DMNDiagramTuple tuple = sessionState.getDRGDiagramTuple();
        assertEquals(dmnDiagram1, tuple.getDMNDiagram());
        assertEquals(stunnerDiagram1, tuple.getStunnerDiagram());
    }

    @Test
    public void testClear() {
        sessionState.clear();

        assertEquals(0, sessionState.getDiagramsByDiagramId().size());
        assertEquals(0, sessionState.getDMNDiagramsByDiagramId().size());
    }

    @Test
    public void testGetModelDRGElements() {

        final DRGElement drgElement1 = mock(DRGElement.class);
        final DRGElement drgElement2 = mock(DRGElement.class);
        final DRGElement drgElement3 = mock(DRGElement.class);
        final DRGElement drgElement4 = mock(DRGElement.class);
        final DRGElement drgElement5 = mock(DRGElement.class);
        final DRGElement drgElement6 = mock(DRGElement.class);

        when(dmnDiagramUtils.getDRGElements(stunnerDiagram1)).thenReturn(asList(drgElement1, drgElement2));
        when(dmnDiagramUtils.getDRGElements(stunnerDiagram2)).thenReturn(asList(drgElement3, drgElement4, drgElement5));
        when(dmnDiagramUtils.getDRGElements(stunnerDiagram3)).thenReturn(singletonList(drgElement6));

        final List<DRGElement> drgElements = sessionState.getModelDRGElements();

        assertTrue(drgElements.contains(drgElement1));
        assertTrue(drgElements.contains(drgElement2));
        assertTrue(drgElements.contains(drgElement3));
        assertTrue(drgElements.contains(drgElement4));
        assertTrue(drgElements.contains(drgElement5));
        assertTrue(drgElements.contains(drgElement6));
    }

    @Test
    public void testGetModelImports() {

        final Definitions definitions = mock(Definitions.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Import import3 = mock(Import.class);
        final Import import4 = mock(Import.class);
        final Import import5 = mock(Import.class);
        final Import import6 = mock(Import.class);

        when(definitions.getImport()).thenReturn(asList(import1, import2, import3, import4, import5, import6));
        when(dmnDiagramUtils.getDefinitions(stunnerDiagram1)).thenReturn(definitions);

        final List<Import> imports = sessionState.getModelImports();

        assertTrue(imports.contains(import1));
        assertTrue(imports.contains(import2));
        assertTrue(imports.contains(import3));
        assertTrue(imports.contains(import4));
        assertTrue(imports.contains(import5));
        assertTrue(imports.contains(import6));
    }
}
