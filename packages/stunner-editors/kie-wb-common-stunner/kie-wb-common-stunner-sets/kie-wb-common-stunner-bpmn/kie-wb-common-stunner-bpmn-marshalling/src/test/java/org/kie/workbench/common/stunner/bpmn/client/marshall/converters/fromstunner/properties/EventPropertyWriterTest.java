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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.RootElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class EventPropertyWriterTest {

    private static final String EMPTY_STRING = "";
    private static final String NON_EMPTY_STRING = "nomEmpty";
    private static final String SAMPLE_STRUCTURE_REF = "my.var.ref";
    protected final static String elementId = "MY_ID";
    private final static String ERROR_CODE = "ERROR_CODE";

    protected Event event;
    protected EventPropertyWriter propertyWriter;

    @Test
    public void testMessageStructureRef() {
        List<ItemDefinition> itemDefinitions = new ArrayList<>();
        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        itemDefinitions.add(itemDefinition);

        when(itemDefinition.getStructureRef()).thenReturn(SAMPLE_STRUCTURE_REF);
        when(propertyWriter.getItemDefinitions()).thenReturn(itemDefinitions);

        MessageRef messageRef1 = new MessageRef("someVar", EMPTY_STRING);
        propertyWriter.addMessage(messageRef1);
        assertEquals(messageRef1.getStructure(), SAMPLE_STRUCTURE_REF);

        MessageRef messageRef2 = new MessageRef("someVar", NON_EMPTY_STRING);
        propertyWriter.addMessage(messageRef2);
        assertEquals(messageRef2.getStructure(), SAMPLE_STRUCTURE_REF);

        itemDefinitions.clear();
        MessageRef messageRef3 = new MessageRef("someVar", NON_EMPTY_STRING);
        propertyWriter.addMessage(messageRef3);
        assertEquals(messageRef3.getStructure(), EMPTY_STRING);
    }

    @Test
    public void testAddEmptyError() {
        final ArgumentCaptor<RootElement> captor = ArgumentCaptor.forClass(RootElement.class);

        ErrorRef errorRef = new ErrorRef();
        propertyWriter.addError(errorRef);
        ErrorEventDefinition definition = getErrorDefinition();
        assertNull(definition.getErrorRef().getErrorCode());
        assertFalse(definition.getErrorRef().getId().isEmpty());

        verify(propertyWriter).addRootElement(captor.capture());
        Error error = (Error) captor.getValue();

        assertNull(error.getErrorCode());
        assertFalse(error.getId().isEmpty());
    }

    @Test
    public void testAddError() {
        final ArgumentCaptor<RootElement> captor = ArgumentCaptor.forClass(RootElement.class);

        ErrorRef errorRef = new ErrorRef();
        errorRef.setValue(ERROR_CODE);
        propertyWriter.addError(errorRef);
        ErrorEventDefinition definition = getErrorDefinition();
        Assert.assertEquals(ERROR_CODE, definition.getErrorRef().getErrorCode());
        assertFalse(definition.getErrorRef().getId().isEmpty());

        verify(propertyWriter).addRootElement(captor.capture());
        Error error = (Error) captor.getValue();

        Assert.assertEquals(ERROR_CODE, error.getErrorCode());
        assertFalse(error.getId().isEmpty());
    }

    public abstract ErrorEventDefinition getErrorDefinition();
}
