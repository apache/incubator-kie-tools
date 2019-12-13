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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.List;

import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jgroups.util.Util.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventPropertyWriterTest {

    private MessageRef messageRef = new MessageRef("someVar", "");

    private static final String sampleStructureRef = "my.var.ref";

    @Mock
    private ItemDefinition itemDefinition;

    @Test
    public void testMessageStructureRef() {
        String elementId = "MY_ID";
        StartEvent startEvent = bpmn2.createStartEvent();
        startEvent.setId(elementId);

        when(itemDefinition.getStructureRef()).thenReturn(sampleStructureRef);
        EventPropertyWriter writer = new EventPropertyWriter(startEvent, new FlatVariableScope()) {

            @Override
            public void setAssignmentsInfo(AssignmentsInfo assignmentsInfo) {

            }

            @Override
            protected void addEventDefinition(EventDefinition eventDefinition) {

            }

            public List<ItemDefinition> getItemDefinitions() {
                itemDefinitions.clear();
                itemDefinitions.add(itemDefinition);
                return itemDefinitions;
            }
        };

        writer.addMessage(messageRef);
        assertEquals(messageRef.getStructure(), sampleStructureRef);

        when(itemDefinition.getStructureRef()).thenReturn(sampleStructureRef);

        messageRef.setStructure("nonEmpty");
        writer.addMessage(messageRef);
        assertEquals(messageRef.getStructure(), "nonEmpty");

        writer = new EventPropertyWriter(startEvent, new FlatVariableScope()) {

            @Override
            public void setAssignmentsInfo(AssignmentsInfo assignmentsInfo) {

            }

            @Override
            protected void addEventDefinition(EventDefinition eventDefinition) {

            }

            public List<ItemDefinition> getItemDefinitions() {
                itemDefinitions.clear();
                return itemDefinitions;
            }
        };

        messageRef.setStructure("");
        writer.addMessage(messageRef);
        assertEquals(messageRef.getStructure(), "");

        messageRef.setStructure("nonEmpty");
        writer.addMessage(messageRef);
        assertEquals(messageRef.getStructure(), "nonEmpty");
    }
}
