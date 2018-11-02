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

package org.kie.workbench.common.stunner.bpmn.definition.property.cm;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.VariablesType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class CaseFileVariablesTest {

    private String _value;
    private String _rawValue;

    @Before
    public void setup() {
        _value = "CFV1:Boolean,CFV2:Boolean,CFV3:Boolean";
        _rawValue = CaseFileVariables.CASE_FILE_PREFIX + "CFV1:Boolean," +
                CaseFileVariables.CASE_FILE_PREFIX + "CFV2:Boolean," +
                CaseFileVariables.CASE_FILE_PREFIX + "CFV3:Boolean";
    }

    @Test
    public void testGetType() {
        CaseFileVariables tested = new CaseFileVariables(_value);
        assertEquals(new VariablesType(), tested.getType());
    }

    @Test
    public void testGetValue() {
        CaseFileVariables tested = new CaseFileVariables(_value);
        assertEquals(_value, tested.getValue());
    }

    @Test
    public void testGetRawValue() {
        CaseFileVariables testedWithValue = new CaseFileVariables(_value);
        assertEquals(testedWithValue.getRawValue(), _rawValue);

        CaseFileVariables testedNoValue = new CaseFileVariables();
        assertEquals(testedNoValue.getRawValue(), "");
    }

    @Test
    public void testHashCode() {
        CaseFileVariables testedWithValue = new CaseFileVariables(_value);
        assertEquals(testedWithValue.hashCode(), -1359743347);
    }

    @Test
    public void testEquals() {
        CaseFileVariables testedWithValue = new CaseFileVariables(_value);
        CaseFileVariables otherEqual = new CaseFileVariables(_value);

        assertFalse(testedWithValue.equals(null));
        assertEquals(testedWithValue, testedWithValue);
        assertEquals(otherEqual, testedWithValue);
        assertEquals(testedWithValue, otherEqual);

        CaseFileVariables otherNotEqual = new CaseFileVariables();
        assertNotEquals(new Object(), testedWithValue);
        assertNotEquals(testedWithValue, new Object());
        assertNotEquals(otherNotEqual, testedWithValue);
        assertNotEquals(testedWithValue, otherNotEqual);
    }
}
