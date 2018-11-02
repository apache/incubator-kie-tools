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

package org.kie.workbench.common.stunner.cm.backend.marshall.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.RootElement;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CaseManagementMarshallerTest {

    private CaseManagementMarshaller marshaller;

    @Before
    public void setUp() throws Exception {
        marshaller = new CaseManagementMarshaller(mock(DefinitionManager.class), mock(OryxManager.class));
    }

    @Test
    public void testRevisitSubProcessItemDefs() throws Exception {
        final List<RootElement> rootElementList = new ArrayList<>(0);
        final Definitions definitions = mock(Definitions.class);
        when(definitions.getRootElements()).thenReturn(rootElementList);

        marshaller.revisitSubProcessItemDefs(definitions);

        assertTrue(rootElementList.isEmpty());
    }

    @Test
    public void testAddSubprocessItemDefs() throws Exception {
        final List<ItemDefinition> itemDefinitionList = new ArrayList<>(3);
        itemDefinitionList.add(mock(ItemDefinition.class));
        itemDefinitionList.add(mock(ItemDefinition.class));
        itemDefinitionList.add(mock(ItemDefinition.class));

        itemDefinitionList.stream().forEach(i -> marshaller.addSubprocessItemDefs(i));

        final List<RootElement> rootElementList = new ArrayList<>(3);
        final Definitions definitions = mock(Definitions.class);
        when(definitions.getRootElements()).thenReturn(rootElementList);

        marshaller.revisitSubProcessItemDefs(definitions);

        assertTrue(IntStream.range(0, itemDefinitionList.size())
                           .allMatch(i -> itemDefinitionList.get(i).equals(rootElementList.get(i))));


        rootElementList.clear();

        marshaller.revisitSubProcessItemDefs(definitions);

        assertTrue(rootElementList.isEmpty());
    }
}