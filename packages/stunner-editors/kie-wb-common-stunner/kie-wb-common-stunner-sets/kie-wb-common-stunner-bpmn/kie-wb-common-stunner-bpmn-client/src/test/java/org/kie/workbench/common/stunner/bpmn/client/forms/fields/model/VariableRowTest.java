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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VariableRowTest {

    private static final int ID = 100;
    private static final int LAST_ID = 50;
    private static final Variable.VariableType VARIABLE_TYPE = Variable.VariableType.PROCESS;
    private static final String NAME = "Variable Name";
    private static final String DATA_TYPE = "Boolean";
    private static final String DATA_TYPE_DISPLAY_NAME = "Boolean Display Name";
    private static final String CUSTOM_DATA_TYPE = "CustomData";
    private static final String TAG_1 = "Tag 1";
    private static final String TAG_2 = "Tag 2";
    private static final String TAG_3 = "Tag 3";
    private static final ArrayList<String> TAGS = new ArrayList<>(Arrays.asList(TAG_1, TAG_2, TAG_3));
    private static final Map<String, String> MAP_DATA_TYPE_NAMES_TO_DISPLAY_NAMES = new HashMap<String, String>() {{
        put(DATA_TYPE, DATA_TYPE_DISPLAY_NAME);
    }};

    @Test
    public void getTags() {
        VariableRow tested = new VariableRow();
        tested.tags = TAGS;
        assertEquals(TAGS, tested.getTags());
    }

    @Test
    public void setTags() {
        VariableRow tested = new VariableRow();
        tested.tags = TAGS;
        assertEquals(TAGS, tested.tags);
    }

    @Test
    public void getId() {
        VariableRow.lastId = LAST_ID;
        VariableRow tested = new VariableRow();
        assertEquals(LAST_ID, tested.id);
        assertEquals(LAST_ID + 1, VariableRow.lastId);

        tested.id = ID;
        assertEquals(ID, tested.getId());
    }

    @Test
    public void setId() {
        VariableRow tested = new VariableRow();
        tested.setId(ID);
        assertEquals(ID, tested.id);
    }

    @Test
    public void getVariableType() {
        VariableRow tested = new VariableRow();
        tested.variableType = VARIABLE_TYPE;
        assertEquals(VARIABLE_TYPE, tested.getVariableType());
    }

    @Test
    public void setVariableType() {
        VariableRow tested = new VariableRow();
        tested.setVariableType(VARIABLE_TYPE);
        assertEquals(VARIABLE_TYPE, tested.variableType);
    }

    @Test
    public void getName() {
        VariableRow tested = new VariableRow();
        tested.name = NAME;
        assertEquals(NAME, tested.getName());
    }

    @Test
    public void setName() {
        VariableRow tested = new VariableRow();
        tested.setName(NAME);
        assertEquals(NAME, tested.name);
    }

    @Test
    public void getDataTypeDisplayName() {
        VariableRow tested1 = new VariableRow();
        tested1.dataTypeDisplayName = DATA_TYPE_DISPLAY_NAME;
        assertEquals(DATA_TYPE_DISPLAY_NAME, tested1.getDataTypeDisplayName());

        Variable variable2 = new Variable(NAME, VARIABLE_TYPE, DATA_TYPE, null, null);
        VariableRow tested2 = new VariableRow(variable2, MAP_DATA_TYPE_NAMES_TO_DISPLAY_NAMES);
        assertEquals(DATA_TYPE_DISPLAY_NAME, tested2.getDataTypeDisplayName());

        String dataType = "Object";
        Variable variable3 = new Variable(NAME, VARIABLE_TYPE, dataType, null, null);
        VariableRow tested3 = new VariableRow(variable3, MAP_DATA_TYPE_NAMES_TO_DISPLAY_NAMES);
        assertEquals(dataType, tested3.getDataTypeDisplayName());
    }

    @Test
    public void setDataTypeDisplayName() {
        VariableRow tested = new VariableRow();
        tested.setDataTypeDisplayName(DATA_TYPE_DISPLAY_NAME);
        assertEquals(DATA_TYPE_DISPLAY_NAME, tested.dataTypeDisplayName);
    }

    @Test
    public void getCustomDataType() {
        VariableRow tested = new VariableRow();
        tested.customDataType = CUSTOM_DATA_TYPE;
        assertEquals(CUSTOM_DATA_TYPE, tested.getCustomDataType());
    }

    @Test
    public void setCustomDataType() {
        VariableRow tested = new VariableRow();
        tested.setCustomDataType(CUSTOM_DATA_TYPE);
        assertEquals(CUSTOM_DATA_TYPE, tested.customDataType);
    }

    @Test
    public void testEquals() {
        VariableRow tested1 = new VariableRow();
        assertTrue(tested1.equals(tested1));

        VariableRow tested2 = new VariableRow();
        assertFalse(tested2.equals(null));

        VariableRow tested3 = new VariableRow();
        assertFalse(tested3.equals(new Object()));

        VariableRow tested4 = new VariableRow();
        tested4.setId(ID);
        VariableRow otherVariableRow4 = new VariableRow();
        otherVariableRow4.setId(ID);
        assertTrue(tested4.equals(otherVariableRow4));

        VariableRow tested5 = new VariableRow();
        tested5.setId(ID);
        VariableRow otherTested5 = new VariableRow();
        otherTested5.setId(LAST_ID);
        assertFalse(tested5.equals(otherTested5));
    }

    @Test
    public void testHashCode() {
        VariableRow tested1 = new VariableRow();
        tested1.setId(ID);
        assertEquals(ID, tested1.hashCode());

        VariableRow tested2 = new VariableRow();
        tested2.setId(LAST_ID);
        assertEquals(LAST_ID, tested2.hashCode());
    }

    @Test
    public void testToString() {
        VariableRow tested = new VariableRow(Variable.VariableType.PROCESS, NAME, DATA_TYPE, CUSTOM_DATA_TYPE, TAGS);
        String expected = "VariableRow [variableType=PROCESS, name=Variable Name, dataTypeDisplayName=Boolean, customDataType=CustomData, tags=Tag 1,Tag 2,Tag 3]";
        assertEquals(expected, tested.toString());
    }
}