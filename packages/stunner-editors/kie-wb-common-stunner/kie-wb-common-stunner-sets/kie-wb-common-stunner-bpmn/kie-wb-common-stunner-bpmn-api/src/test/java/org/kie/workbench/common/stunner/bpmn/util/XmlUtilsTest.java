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


package org.kie.workbench.common.stunner.bpmn.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.util.XmlUtils.createValidId;

public class XmlUtilsTest {

    @Test
    public void testIncorrectNcNameStartCharacters() {
        assertFalse(XmlUtils.isNcNameStartCharacter('1'));
        assertFalse(XmlUtils.isNcNameStartCharacter('<'));
        assertFalse(XmlUtils.isNcNameStartCharacter('-'));
        assertFalse(XmlUtils.isNcNameStartCharacter('#'));
    }

    @Test
    public void testCorrectNcNameStartCharacters() {
        assertTrue(XmlUtils.isNcNameStartCharacter('a'));
        assertTrue(XmlUtils.isNcNameStartCharacter('A'));
        assertTrue(XmlUtils.isNcNameStartCharacter('_'));
    }

    @Test
    public void testIncorrectNcCharacter() {
        assertFalse(XmlUtils.isNcNameCharacter('&'));
        assertFalse(XmlUtils.isNcNameCharacter('$'));
        assertFalse(XmlUtils.isNcNameCharacter('%'));
        assertFalse(XmlUtils.isNcNameCharacter('>'));
        assertFalse(XmlUtils.isNcNameCharacter('£'));
        assertFalse(XmlUtils.isNcNameCharacter(' '));
    }

    @Test
    public void testValidNcCharacter() {
        assertTrue(XmlUtils.isNcNameCharacter('1'));
        assertTrue(XmlUtils.isNcNameCharacter('_'));
        assertTrue(XmlUtils.isNcNameCharacter('-'));
        assertTrue(XmlUtils.isNcNameCharacter('a'));
        assertTrue(XmlUtils.isNcNameCharacter('Ф'));
        assertTrue(XmlUtils.isNcNameCharacter('月'));
    }

    @Test
    public void testDefaultValidName() {
        assertEquals("_", createValidId(""));
        assertEquals("_", createValidId(null));
        assertEquals("_", createValidId("&"));
        assertEquals("_", createValidId("#"));
        assertEquals("_", createValidId(" "));
        assertEquals("_", createValidId("£"));
    }

    @Test
    public void testFullyValidName() {
        String name = "SomeValidNameForTheПроцесс";
        assertEquals(name, createValidId(name));
    }

    @Test
    public void testSomeSymbolsCleared() {
        String name = "Hello $& Name";
        assertEquals("HelloName", createValidId(name));
    }

    @Test
    public void testCreateValidIdWhenFirstSymbolIsNotCorrectNcStart() {
        String name = "1name";
        assertEquals("_1name", createValidId(name));
    }
}
