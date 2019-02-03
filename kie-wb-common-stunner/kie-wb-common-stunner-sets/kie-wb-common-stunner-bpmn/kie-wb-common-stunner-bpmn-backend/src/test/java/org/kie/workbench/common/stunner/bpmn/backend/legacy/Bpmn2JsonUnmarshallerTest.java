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
package org.kie.workbench.common.stunner.bpmn.backend.legacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Bpmn2JsonUnmarshallerTest {

    private Bpmn2JsonUnmarshaller tested;

    @Before
    public void init() throws Exception {
        tested = new Bpmn2JsonUnmarshaller();
    }

    @Test
    public void testUpdateIDs() {
        Definitions defs = mock(Definitions.class);
        Process process = mock(Process.class);
        DataObject flowElement = mock(DataObject.class);

        final Value<String> processId = new Value<>("Project:Bad Id");
        final Value<String> flowElementId = new Value<>("Bad Flow Id!");

        when(process.getId()).thenAnswer((m) -> processId.get());
        doAnswer((m) -> {
            processId.set(m.getArgumentAt(0, String.class));
            return null;
        }).when(process).setId(anyString());

        when(flowElement.getId()).thenAnswer((m) -> flowElementId.get());
        when(flowElement.getName()).thenAnswer((m) -> flowElementId.get());
        doAnswer((m) -> {
            flowElementId.set(m.getArgumentAt(0, String.class));
            return null;
        }).when(flowElement).setId(anyString());

        when(process.getFlowElements()).thenReturn(Arrays.asList(flowElement));
        when(defs.getRootElements()).thenReturn(Arrays.asList(process));

        tested.updateIDs(defs);

        assertEquals("Project:BadId", processId.get());
        assertEquals("BadFlowId", flowElementId.get());
    }

    private static final class Value<T> {

        T value;

        public Value(T value) {
            this.value = value;
        }

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return this.value;
        }
    }

    @Test
    public void testAddSubprocessItemDefs() throws Exception {
        final List<ItemDefinition> itemDefinitionList = new ArrayList<>(3);

        final ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        when(itemDefinition1.getId()).thenReturn("mockItemDefinition1");
        itemDefinitionList.add(itemDefinition1);

        final ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        when(itemDefinition2.getId()).thenReturn("mockItemDefinition2");
        itemDefinitionList.add(itemDefinition2);

        final ItemDefinition itemDefinition3 = mock(ItemDefinition.class);
        when(itemDefinition3.getId()).thenReturn("mockItemDefinition3");
        itemDefinitionList.add(itemDefinition3);

        itemDefinitionList.stream().forEach(i -> tested.addSubprocessItemDefs(i));

        final List<RootElement> rootElementList = new ArrayList<>(3);
        final Definitions definitions = mock(Definitions.class);
        when(definitions.getRootElements()).thenReturn(rootElementList);

        tested.revisitSubProcessItemDefs(definitions);
        assertTrue(rootElementList.containsAll(itemDefinitionList));

        rootElementList.clear();
        tested.revisitSubProcessItemDefs(definitions);
        assertTrue(rootElementList.isEmpty());
    }
}
