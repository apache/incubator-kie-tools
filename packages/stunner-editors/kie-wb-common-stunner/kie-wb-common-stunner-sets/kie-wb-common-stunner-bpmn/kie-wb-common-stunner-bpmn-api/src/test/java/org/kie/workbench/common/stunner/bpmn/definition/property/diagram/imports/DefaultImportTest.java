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


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport.DELIMITER;
import static org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport.IDENTIFIER;

public class DefaultImportTest {

    private static final String CLASS_NAME = "ClassName";
    private static final String STRING_EMPTY = "";

    @Test
    public void getClassName() {
        DefaultImport tested = new DefaultImport(CLASS_NAME);
        assertEquals(CLASS_NAME, tested.getClassName());
    }

    @Test
    public void setClassName() {
        DefaultImport tested = new DefaultImport();
        tested.setClassName(CLASS_NAME);
        assertEquals(CLASS_NAME, tested.getClassName());
    }

    @Test
    public void testEquals() {
        DefaultImport tested1 = new DefaultImport();
        DefaultImport tested2 = new DefaultImport();
        assertNotEquals(tested1, tested2);

        DefaultImport tested3 = new DefaultImport(CLASS_NAME);
        DefaultImport tested4 = new DefaultImport(CLASS_NAME);
        assertNotEquals(tested3, tested4);
    }

    @Test
    public void testHashCode() {
        DefaultImport tested1 = new DefaultImport();
        DefaultImport tested2 = new DefaultImport();
        assertNotEquals(tested1.hashCode(), tested2.hashCode());

        DefaultImport tested3 = new DefaultImport(CLASS_NAME);
        DefaultImport tested4 = new DefaultImport(CLASS_NAME);
        assertNotEquals(tested3.hashCode(), tested4.hashCode());
    }

    @Test
    public void testToString() {
        DefaultImport tested1 = new DefaultImport();
        assertEquals(STRING_EMPTY, tested1.toString());

        DefaultImport tested2 = new DefaultImport(STRING_EMPTY);
        assertEquals(STRING_EMPTY, tested2.toString());

        DefaultImport tested3 = new DefaultImport(CLASS_NAME);
        assertEquals(IDENTIFIER + DELIMITER + CLASS_NAME, tested3.toString());
    }

    @Test
    public void isValidString() {
        String validString = IDENTIFIER + DELIMITER + CLASS_NAME;
        assertTrue(DefaultImport.isValidString(validString));

        String invalidString1 = IDENTIFIER;
        assertFalse(DefaultImport.isValidString(invalidString1));

        String invalidString2 = IDENTIFIER + DELIMITER + CLASS_NAME + DELIMITER + "invalid";
        assertFalse(DefaultImport.isValidString(invalidString2));

        String invalidString3 = "invalid" + DELIMITER + CLASS_NAME;
        assertFalse(DefaultImport.isValidString(invalidString3));

        String invalidString4 = IDENTIFIER + DELIMITER + STRING_EMPTY;
        assertFalse(DefaultImport.isValidString(invalidString4));
    }

    @Test
    public void fromStringValid() throws Exception {
        String validString = IDENTIFIER + DELIMITER + CLASS_NAME;
        DefaultImport validImport = DefaultImport.fromString(validString);
        assertEquals(CLASS_NAME, validImport.getClassName());
    }

    @Test(expected = Exception.class)
    public void fromStringInvalid() throws Exception {
        String invalidString = "invalid" + DELIMITER + CLASS_NAME;
        DefaultImport.fromString(invalidString);
    }
}