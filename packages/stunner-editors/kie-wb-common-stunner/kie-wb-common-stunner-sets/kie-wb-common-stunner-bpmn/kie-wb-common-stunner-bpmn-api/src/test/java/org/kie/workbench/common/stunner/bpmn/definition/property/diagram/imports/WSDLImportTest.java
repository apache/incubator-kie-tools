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
import static org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport.DELIMITER;
import static org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport.IDENTIFIER;

public class WSDLImportTest {

    private static final String LOCATION = "Location";
    private static final String NAMESPACE = "Namespace";
    private static final String STRING_EMPTY = "";

    @Test
    public void getLocation() {
        WSDLImport tested = new WSDLImport(LOCATION, NAMESPACE);
        assertEquals(LOCATION, tested.getLocation());
    }

    @Test
    public void setLocation() {
        WSDLImport tested = new WSDLImport();
        tested.setLocation(LOCATION);
        assertEquals(LOCATION, tested.getLocation());
    }

    @Test
    public void getNamespace() {
        WSDLImport tested = new WSDLImport(LOCATION, NAMESPACE);
        assertEquals(NAMESPACE, tested.getNamespace());
    }

    @Test
    public void setNamespace() {
        WSDLImport tested = new WSDLImport();
        tested.setNamespace(NAMESPACE);
        assertEquals(NAMESPACE, tested.getNamespace());
    }

    @Test
    public void testEquals() {
        WSDLImport tested1 = new WSDLImport();
        WSDLImport tested2 = new WSDLImport();
        assertNotEquals(tested1, tested2);

        WSDLImport tested3 = new WSDLImport(LOCATION, NAMESPACE);
        WSDLImport tested4 = new WSDLImport(LOCATION, NAMESPACE);
        assertNotEquals(tested3, tested4);
    }

    @Test
    public void testHashCode() {
        WSDLImport tested1 = new WSDLImport();
        WSDLImport tested2 = new WSDLImport();
        assertNotEquals(tested1.hashCode(), tested2.hashCode());

        WSDLImport tested3 = new WSDLImport(LOCATION, NAMESPACE);
        WSDLImport tested4 = new WSDLImport(LOCATION, NAMESPACE);
        assertNotEquals(tested3.hashCode(), tested4.hashCode());
    }

    @Test
    public void testToString() {
        WSDLImport tested1 = new WSDLImport();
        assertEquals(STRING_EMPTY, tested1.toString());

        WSDLImport tested2 = new WSDLImport(STRING_EMPTY, STRING_EMPTY);
        assertEquals(STRING_EMPTY, tested2.toString());

        WSDLImport tested3 = new WSDLImport(LOCATION, NAMESPACE);
        assertEquals(tested3.toString(), IDENTIFIER + DELIMITER + LOCATION + DELIMITER + NAMESPACE);
    }

    @Test
    public void isValidString() {
        String validString = IDENTIFIER + DELIMITER + LOCATION + DELIMITER + NAMESPACE;
        assertTrue(WSDLImport.isValidString(validString));

        String invalidString1 = IDENTIFIER + DELIMITER + LOCATION;
        assertFalse(WSDLImport.isValidString(invalidString1));

        String invalidString2 = IDENTIFIER + DELIMITER + LOCATION + DELIMITER + NAMESPACE + DELIMITER + "invalid";
        assertFalse(WSDLImport.isValidString(invalidString2));

        String invalidString3 = "invalid" + DELIMITER + LOCATION + DELIMITER + NAMESPACE;
        assertFalse(WSDLImport.isValidString(invalidString3));

        String invalidString4 = IDENTIFIER + DELIMITER + STRING_EMPTY + DELIMITER + STRING_EMPTY;
        assertFalse(WSDLImport.isValidString(invalidString4));

        String invalidString5 = IDENTIFIER + DELIMITER + STRING_EMPTY + DELIMITER + NAMESPACE;
        assertFalse(WSDLImport.isValidString(invalidString5));

        String invalidString6 = IDENTIFIER + DELIMITER + LOCATION + DELIMITER + STRING_EMPTY;
        assertFalse(WSDLImport.isValidString(invalidString6));
    }

    @Test
    public void fromStringValid() throws Exception {
        String validString = IDENTIFIER + DELIMITER + LOCATION + DELIMITER + NAMESPACE;
        WSDLImport validImport = WSDLImport.fromString(validString);
        assertEquals(LOCATION, validImport.getLocation());
        assertEquals(NAMESPACE, validImport.getNamespace());
    }

    @Test(expected = Exception.class)
    public void fromStringInvalid() throws Exception {
        String invalidString = "invalid" + DELIMITER + LOCATION + DELIMITER + NAMESPACE;
        WSDLImport.fromString(invalidString);
    }
}