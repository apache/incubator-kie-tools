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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.DIVIDER;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.TAG_DIVIDER;

public class VariableTest {

    private static final String NAME = "Variable Name";
    private static final String DATA_TYPE = "Boolean";
    private static final String CUSTOM_DATA_TYPE = "Variable Custom Data Type";
    private static final String TAG_1 = "Tag 1";
    private static final String TAG_2 = "Tag 2";
    private static final String TAG_3 = "Tag 3";
    private static final ArrayList<String> TAGS = new ArrayList<>(Arrays.asList(TAG_1, TAG_2, TAG_3));
    private static final ArrayList<String> OTHER_TAGS = new ArrayList<>(Arrays.asList("Other Tag 1", "Other Tag 2"));

    @Test
    public void testTags() {
        Variable variable1 = new Variable(Variable.VariableType.PROCESS);
        assertNull(variable1.getTags());

        variable1.setTags(TAGS);
        assertEquals(TAGS, variable1.getTags());

        variable1.setTags(null);
        assertNull(variable1.getTags());

        Variable variable2 = new Variable(null, Variable.VariableType.PROCESS, null, null, TAGS);
        assertEquals(TAGS, variable2.getTags());
    }

    @Test
    public void testVariableType() {
        Variable variable = new Variable(Variable.VariableType.INPUT);
        assertEquals(Variable.VariableType.INPUT, variable.getVariableType());

        variable.setVariableType(Variable.VariableType.OUTPUT);
        assertEquals(Variable.VariableType.OUTPUT, variable.getVariableType());

        variable.setVariableType(null);
        assertNull(variable.getVariableType());
    }

    @Test
    public void testName() {
        Variable variable = new Variable(Variable.VariableType.PROCESS);
        assertNull(variable.getName());

        variable.setName(NAME);
        assertEquals(NAME, variable.getName());

        variable.setName(null);
        assertNull(variable.getName());
    }

    @Test
    public void testDataType() {
        Variable variable = new Variable(Variable.VariableType.PROCESS);
        assertNull(variable.getDataType());

        variable.setDataType(DATA_TYPE);
        assertEquals(DATA_TYPE, variable.getDataType());

        variable.setDataType(null);
        assertNull(variable.getDataType());
    }

    @Test
    public void testCustomDataType() {
        Variable variable = new Variable(Variable.VariableType.PROCESS);
        assertNull(variable.getCustomDataType());

        variable.setCustomDataType(CUSTOM_DATA_TYPE);
        assertEquals(CUSTOM_DATA_TYPE, variable.getCustomDataType());

        variable.setCustomDataType(null);
        assertNull(variable.getCustomDataType());
    }

    @Test
    public void testToString() {
        Variable variable1 = new Variable(null, Variable.VariableType.PROCESS);
        assertNull(variable1.toString());

        Variable variable2 = new Variable("", Variable.VariableType.PROCESS);
        assertNull(variable2.toString());

        Variable variable3 = new Variable(NAME, Variable.VariableType.PROCESS);
        String result3 = NAME + DIVIDER + DIVIDER;
        assertEquals(result3, variable3.toString());

        Variable variable4 = new Variable(NAME, Variable.VariableType.PROCESS, null, "");
        String result4 = NAME + DIVIDER + DIVIDER;
        assertEquals(result4, variable4.toString());

        Variable variable5 = new Variable(NAME, Variable.VariableType.PROCESS, null, CUSTOM_DATA_TYPE);
        String result5 = NAME + DIVIDER + CUSTOM_DATA_TYPE + DIVIDER;
        assertEquals(result5, variable5.toString());

        Variable variable6 = new Variable(NAME, Variable.VariableType.PROCESS, "", null);
        String result6 = NAME + DIVIDER + DIVIDER;
        assertEquals(result6, variable6.toString());

        Variable variable7 = new Variable(NAME, Variable.VariableType.PROCESS, DATA_TYPE, null);
        String result7 = NAME + DIVIDER + DATA_TYPE + DIVIDER;
        assertEquals(result7, variable7.toString());

        Variable variable8 = new Variable(NAME, Variable.VariableType.INPUT, null, null, TAGS);
        String result8 = NAME + DIVIDER + DIVIDER;
        assertEquals(result8, variable8.toString());

        Variable variable9 = new Variable(NAME, Variable.VariableType.PROCESS, null, null, TAGS);
        String result9 = NAME + DIVIDER + DIVIDER + TAG_1 + TAG_DIVIDER + TAG_2 + TAG_DIVIDER + TAG_3;
        assertEquals(result9, variable9.toString());
    }

    @Test
    public void testDeserialize() {
        Variable.VariableType variableType = Variable.VariableType.PROCESS;

        String test1 = "";
        Variable result1 = Variable.deserialize(test1, variableType);
        Variable expected1 = new Variable(variableType);
        assertEquals(expected1, result1);

        String test2 = DIVIDER + DIVIDER;
        Variable result2 = Variable.deserialize(test2, variableType);
        Variable expected2 = new Variable("", variableType);
        assertEquals(expected2, result2);

        String test3 = NAME + DIVIDER + DIVIDER;
        Variable result3 = Variable.deserialize(test3, variableType);
        Variable expected3 = new Variable(NAME, variableType);
        assertEquals(expected3, result3);

        String test4 = NAME + DIVIDER + DATA_TYPE + DIVIDER;
        Variable result4 = Variable.deserialize(test4, variableType, Arrays.asList(DATA_TYPE));
        Variable expected4 = new Variable(NAME, variableType, DATA_TYPE, null);
        assertEquals(expected4, result4);

        String test5 = NAME + DIVIDER + CUSTOM_DATA_TYPE + DIVIDER;
        Variable result5 = Variable.deserialize(test5, variableType, null);
        Variable expected5 = new Variable(NAME, variableType, null, CUSTOM_DATA_TYPE);
        assertEquals(expected5, result5);

        String test6 = NAME + DIVIDER + CUSTOM_DATA_TYPE + DIVIDER;
        Variable result6 = Variable.deserialize(test6, variableType, Arrays.asList(DATA_TYPE));
        Variable expected6 = new Variable(NAME, variableType, null, CUSTOM_DATA_TYPE);
        assertEquals(expected6, result6);

        String test7 = NAME + DIVIDER + DIVIDER + "[" + TAG_1 + TAG_DIVIDER + TAG_2 + TAG_DIVIDER + TAG_3 + "]";
        Variable result7 = Variable.deserialize(test7, variableType, null);
        Variable expected7 = new Variable(NAME, variableType, null, null, TAGS);
        assertEquals(expected7, result7);
    }

    @Test
    public void testEquals() {
        //=
        Variable variable1 = new Variable(NAME, Variable.VariableType.PROCESS, DATA_TYPE, CUSTOM_DATA_TYPE, TAGS);
        assertTrue(variable1.equals(variable1));

        //INSTANCE
        Variable variable2 = new Variable(null);
        assertFalse(variable2.equals(new Object()));

        //VARIABLE TYPE
        Variable variable3 = new Variable(Variable.VariableType.PROCESS);
        Variable otherVariable3 = new Variable(Variable.VariableType.INPUT);
        assertFalse(variable3.equals(otherVariable3));

        //NAME
        Variable variable4 = new Variable(NAME, null);
        Variable otherVariable4 = new Variable("Other Name", null);
        assertFalse(variable4.equals(otherVariable4));

        Variable variable5 = new Variable(null, null, null, null);
        Variable otherVariable5 = new Variable(NAME, null);
        assertFalse(variable5.equals(otherVariable5));

        //DATA TYPE
        Variable variable6 = new Variable(null, null, DATA_TYPE, null);
        Variable otherVariable6 = new Variable(null, null, "Object", null);
        assertFalse(variable6.equals(otherVariable6));

        Variable variable7 = new Variable(null, null, null, null);
        Variable otherVariable7 = new Variable(null, null, DATA_TYPE, null);
        assertFalse(variable7.equals(otherVariable7));

        Variable variable8 = new Variable(null, null, null, null);
        Variable otherVariable8 = new Variable(null, null, "", null);
        assertTrue(variable8.equals(otherVariable8));

        //CUSTOM DATA TYPE
        Variable variable9 = new Variable(null, null, null, CUSTOM_DATA_TYPE);
        Variable otherVariable9 = new Variable(null, null, null, "Other Custom Data Type");
        assertFalse(variable9.equals(otherVariable9));

        Variable variable10 = new Variable(null, null, null, null);
        Variable otherVariable10 = new Variable(null, null, null, CUSTOM_DATA_TYPE);
        assertFalse(variable10.equals(otherVariable10));

        Variable variable11 = new Variable(null, null, null, null);
        Variable otherVariable11 = new Variable(null, null, null, "");
        assertTrue(variable11.equals(otherVariable11));

        //TAGS
        Variable variable12 = new Variable(null, null, null, null, TAGS);
        Variable otherVariable12 = new Variable(null, null, null, null, OTHER_TAGS);
        assertFalse(variable12.equals(otherVariable12));

        Variable variable13 = new Variable(null, null, null, null, null);
        Variable otherVariable13 = new Variable(null, null, null, null, TAGS);
        assertFalse(variable13.equals(otherVariable13));

        Variable variable14 = new Variable(null, null, null, null, new ArrayList<>());
        Variable otherVariable14 = new Variable(null, null, null, null, TAGS);
        assertFalse(variable14.equals(otherVariable14));
    }

    @Test
    public void testHashCode() {
        Variable variable1 = new Variable(null, null, null, null, null);
        assertEquals(0, variable1.hashCode());

        Variable variable2 = new Variable(Variable.VariableType.PROCESS);
        assertNotEquals(0, variable2.hashCode());

        Variable variable3 = new Variable(NAME, null, DATA_TYPE, CUSTOM_DATA_TYPE, TAGS);
        assertEquals(1153831327, variable3.hashCode());

        Variable variable4 = new Variable(null, null, null, null, new ArrayList<>());
        assertEquals(0, variable4.hashCode());
    }
}